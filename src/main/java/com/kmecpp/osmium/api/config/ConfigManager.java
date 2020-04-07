package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.IOUtil;

public class ConfigManager {

	private static final HashMap<OsmiumPlugin, HashSet<Class<?>>> pluginConfigs = new HashMap<>();
	private static final HashMap<Class<?>, ConfigData> configData = new HashMap<>();

	//	public static void main(String[] args) throws Exception {
	//		TypeData.parse("java.util.ArrayList<java.lang.String>");
	//		TypeData.parse("java.util.HashMap<java.lang.String,java.lang.String>");
	//		TypeData data = TypeData.parse("java.util.HashMap<java.util.HashMap<java.lang.String,java.lang.String>,java.util.ArrayList<java.util.HashSet<java.lang.String>>>");
	//		TypeData data = TypeData.parse("java.util.HashMap<java.lang.String,java.lang.Integer>");
	//		TypeData data = TypeData.parse("int");
	//		System.out.println("FINAL RESULT: " + data);
	//		walk(Config.class, s -> {
	//			System.out.println(s);
	//		});

	//		PluginConfigTypeData data = PluginConfigTypeData.parse(IOUtil.readLines(Paths.get("CONFIG_TYPES").toFile()));
	//		System.out.println(data.get(Config.class));
	//		walk(CoreOsmiumConfig.class);
	//		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(Paths.get("test.conf")).build();
	//		CommentedConfigurationNode root = loader.load();
	//		root.getValue("regions");
	//		System.out.println(root.getNode("regions").getValue().getClass());
	//		System.out.println(root);

	//		System.out.println(getConfigData(ConfigReal.class));
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
				.map(configData::get)
				.filter(Objects::nonNull) //If the config is manual load it will be in pluginConfigs but not configs
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
		ConfigData data = configData.get(configClass);
		if (data != null) {
			return data;
		}

		ConfigClass configProperties = configClass.getDeclaredAnnotation(ConfigClass.class);

		//GET PLUGIN SPECIFIC DATA
		HashMap<String, TypeData> fieldTypeMap;
		Path configPath;
		if (Platform.isDev()) {
			try {
				fieldTypeMap = PluginConfigTypeData.parse(IOUtil.readLines(Paths.get("CONFIG_TYPES").toFile())).get(configClass);
				configPath = Paths.get(configProperties.path());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			OsmiumPlugin plugin = Osmium.getPlugin(configClass);
			try {
				fieldTypeMap = plugin.getConfigTypeData().get(configClass);
				configPath = Osmium.getPlugin(configClass).getFolder().resolve(configProperties.path());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}

		}

		HashMap<String, FieldData> fieldDataMap = new HashMap<>();

		int truncate = configClass.getName().length() + 1;
		walk(configClass, (field) -> {
			String enclosingPath = field.getDeclaringClass().getName().replace('$', '.').toLowerCase() + ".";

			Setting setting = field.getAnnotation(Setting.class);
			String name = getName(field, setting);
			String virtualPath = (enclosingPath + name).substring(truncate); //Truncate must come last
			String physicalPath = (enclosingPath + field.getName()).substring(truncate); //Truncate must come last

			TypeData typeData = fieldTypeMap.get(physicalPath);
			if (typeData == null) {
				typeData = new TypeData(field.getType(), Collections.emptyList());
			}
			FieldData fieldData = new FieldData(field, name, setting, typeData);
			fieldDataMap.put(virtualPath, fieldData);
		});

		data = new ConfigData(configClass, configProperties, configPath, fieldDataMap);
		configData.put(configClass, data);
		return data;
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
			if (field.isAnnotationPresent(Transient.class) || Modifier.isFinal(field.getModifiers()) || !Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			processor.accept(field);
		}

		for (Class<?> nestedClass : cls.getDeclaredClasses()) {
			if (nestedClass.isAnnotationPresent(Transient.class)) {
				continue;
			}
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
					sb.append('-');
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
