package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.IOUtil;

public class ConfigManager {

	private static final HashMap<OsmiumPlugin, HashSet<Class<?>>> pluginConfigs = new HashMap<>();
	private static final HashMap<Class<?>, ConfigData> configs = new HashMap<>();

	//	public static void main(String[] args) throws Exception {
	//		//		TypeData.parse("java.util.ArrayList<java.lang.String>");
	//		//		TypeData.parse("java.util.HashMap<java.lang.String,java.lang.String>");
	//		//		TypeData data = TypeData.parse("java.util.HashMap<java.util.HashMap<java.lang.String,java.lang.String>,java.util.ArrayList<java.util.HashSet<java.lang.String>>>");
	//		//		TypeData data = TypeData.parse("java.util.HashMap<java.lang.String,java.lang.Integer>");
	//		//		TypeData data = TypeData.parse("int");
	//		//		System.out.println("FINAL RESULT: " + data);
	//		//		walk(Config.class, s -> {
	//		//			System.out.println(s);
	//		//		});
	//
	//		//		PluginConfigTypeData data = PluginConfigTypeData.parse(IOUtil.readLines(Paths.get("CONFIG_TYPES").toFile()));
	//		//		System.out.println(data.get(Config.class));
	//		//		walk(CoreOsmiumConfig.class);
	//		//		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(Paths.get("test.conf")).build();
	//		//		CommentedConfigurationNode root = loader.load();
	//		//		root.getValue("regions");
	//		//		System.out.println(root.getNode("regions").getValue().getClass());
	//		//		System.out.println(root);
	//
	//		//		System.out.println(getConfigData(ConfigReal.class));
	//		ConfigManager m = new ConfigManager();
	//		m.load(ConfigReal.class);
	//	}

	public void register(OsmiumPlugin plugin, Class<?> configClass) {
		pluginConfigs.computeIfAbsent(plugin, p -> new HashSet<>()).add(configClass);
	}

	public HashSet<Class<?>> getPluginConfigs(OsmiumPlugin plugin) {
		return pluginConfigs.getOrDefault(plugin, new HashSet<>());
	}

	public void lateInit() {
		pluginConfigs.entrySet().stream()
				.flatMap(e -> e.getValue().stream())
				.map(configs::get)
				.filter(ConfigData::isLoadLate)
				.forEach(configData -> {
					try {
						load(configData.configClass);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
	}

	private ConfigData getConfigData(Class<?> configClass) {
		ConfigData data = configs.get(configClass);
		if (data != null) {
			return data;
		}

		HashMap<String, TypeData> fieldTypeMap = getFieldTypeMap(configClass);

		ConfigClass configProperties = configClass.getDeclaredAnnotation(ConfigClass.class);
		Path configPath = Paths.get(configProperties.path());
		HashMap<String, FieldData> fieldDataMap = new HashMap<>();

		int truncate = configClass.getName().length() + 1;
		walk(configClass, (field) -> {
			String enclosingPath = field.getDeclaringClass().getName().replace('$', '.') + ".";

			Setting setting = field.getAnnotation(Setting.class);
			String name = getName(field, setting);
			String virtualPath = (enclosingPath + name).substring(truncate); //Truncate must come last
			String physicalPath = (enclosingPath + field.getName()).substring(truncate); //Truncate must come last

			FieldData fieldData = new FieldData(field, name, setting, fieldTypeMap.get(physicalPath));
			fieldDataMap.put(virtualPath, fieldData);
		});

		data = new ConfigData(configClass, configProperties, configPath, fieldDataMap);
		configs.put(configClass, data);
		return data;
	}

	private static HashMap<String, TypeData> getFieldTypeMap(Class<?> configClass) {
		if (Platform.isDev()) {
			try {
				return PluginConfigTypeData.parse(IOUtil.readLines(Paths.get("CONFIG_TYPES").toFile())).get(configClass);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		OsmiumPlugin plugin = Osmium.getPlugin(configClass);
		HashMap<String, TypeData> fieldTypeMap;
		try {
			fieldTypeMap = plugin.getConfigTypeData().get(configClass);
			return fieldTypeMap;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void load(Class<?> configClass) throws IOException {
		getConfigData(configClass).load();
	}

	public void save(Class<?> configClass) throws IOException {
		getConfigData(configClass).save();
	}

	//	@ConfigClass(path = "hub/entity-portals.conf")
	//	public static class ConfigReal {
	//
	//		@Setting
	//		public static HashMap<String, WorldPosition> position = new HashMap<>();
	//
	//		public static float b;
	//		public static int a;
	//
	//		public static class Config2 {
	//
	//			public static ArrayList<String> camelCaseList;
	//
	//			@Setting(name = "SET")
	//			public static HashSet<Integer> set;
	//
	//			public static int Config3 = 3;
	//
	//			public static class Config3 {
	//
	//				public static HashMap<String, ArrayList<HashSet<String>>> map = new HashMap<>();
	//
	//				public static HashSet<Integer> set;
	//
	//			}
	//
	//		}
	//
	//	}

	private static void walk(Class<?> cls, Consumer<Field> processor) {
		for (Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(Transient.class)) {
				continue;
			}

			processor.accept(field);
		}

		for (Class<?> nestedClass : cls.getDeclaredClasses()) {
			walk(nestedClass, processor);
		}
	}

	private static String getName(Field field, Setting setting) {
		if (setting == null || setting.name().isEmpty()) {
			StringBuilder sb = new StringBuilder();

			boolean prev = false;
			String name = field.getName();
			for (int i = 0; i < name.length(); i++) {
				char c = name.charAt(i);
				char lower = Character.toLowerCase(c);
				if (i > 0 && c != lower && !prev) {
					sb.append('_');
				}
				sb.append(lower);
				prev = c != lower;
			}
			return sb.toString();
		} else {
			return setting.name();
		}
	}

}
