package com.kmecpp.osmium.api.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.IOUtil;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;

public class ConfigManager {

	private static final HashMap<OsmiumPlugin, HashSet<Class<?>>> pluginConfigs = new HashMap<>();
	private static final HashMap<Class<?>, ConfigData> configData = new HashMap<>();

	private static final HashMap<Class<?>, ClassTypeData> globalTypeData = new HashMap<>();

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

	public void unregister(OsmiumPlugin plugin) {
		HashSet<Class<?>> pluginConfigClasses = pluginConfigs.remove(plugin);
		if (pluginConfigClasses != null) { //Plugin may not have any configs
			for (Class<?> config : pluginConfigClasses) {
				configData.remove(config);
				globalTypeData.remove(config);
			}
		}
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
					} catch (Throwable t) {
						t.printStackTrace();
					}
				});
	}

	private ConfigData getConfigData(Class<?> configClass) {
		ConfigData data = configData.get(configClass);
		if (data != null) {
			return data;
		}

		ConfigClass configProperties = configClass.getDeclaredAnnotation(ConfigClass.class);

		if (configProperties == null) {
			throw new IllegalArgumentException("Class is not annotated with @" + ConfigClass.class.getSimpleName() + ": " + configClass);
		}

		//GET PLUGIN SPECIFIC DATA
		PluginConfigTypeData pluginData;
		Path configPath;
		if (Platform.isDev()) {
			try {
				pluginData = PluginConfigTypeData.parse(null, Arrays.asList(IOUtil.readLines(Paths.get("CONFIG_TYPES").toFile())));
				configPath = Paths.get(configProperties.path());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			OsmiumPlugin plugin = Osmium.getPlugin(configClass);
			pluginData = plugin.getConfigTypeData();
			configPath = Osmium.getPlugin(configClass).getFolder().resolve(configProperties.path());
		}

		HashMap<Class<?>, ClassTypeData> configTypeData;
		try {
			configTypeData = pluginData.getForConfigClass(configClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		//		for (Entry<Class<?>, ClassTypeData> entry : configTypeData.entrySet()) {
		//generateFieldTypeMap(cls, classTypeData)
		//		}

		globalTypeData.putAll(configTypeData);
		//		System.out.println("------------------------------------");
		//		for (Entry<Class<?>, ClassTypeData> entry : configTypeData.entrySet()) {
		//			System.out.println();
		//		}

		//		System.out.println("CONFIG CLASS: " + configClass);
		//		System.out.println("FIELD TYPE MAP: " + configTypeData);

		HashMap<String, FieldData> fieldDataMap = new HashMap<>();
		Reflection.walk(configClass, true, true, (field) -> {
			Setting setting = field.getAnnotation(Setting.class);
			String name = getName(field, setting);
			String virtualPath = getVirtualPath(field, name);

			FieldTypeData typeData = configTypeData.get(configClass).get(field);

			//			typeData.walk(subTypeData -> {
			//				System.out.println("SUB TYPE DATA: " + subTypeData);
			//				if (subTypeData.getClass().isAnnotationPresent(ConfigSerializable.class)) {
			//
			//				}
			//			});
			//			System.out.println();
			//			System.out.println("LOADED TYPE DATA FOR " + virtualPath + " == " + typeData);
			FieldData fieldData = new FieldData(field, name, setting, typeData);
			fieldDataMap.put(virtualPath, fieldData);
		});

		data = new ConfigData(pluginData, configClass, configProperties, configPath, fieldDataMap);
		configData.put(configClass, data);
		return data;
	}

	//	private static HashMap<String, FieldData> generateFieldTypeMap(Class<?> cls, ClassTypeData classTypeData) {
	//		HashMap<String, FieldData> result = new HashMap<>();
	//		Reflection.walk(cls, true, (field) -> {
	//			Setting setting = field.getAnnotation(Setting.class);
	//			String name = getName(field, setting);
	//			String virtualPath = getVirtualPath(field, name);
	//
	//			FieldTypeData typeData = classTypeData.get(field);
	//
	//			globalTypeData.putAll(configTypeData);
	//
	//			System.out.println("LOADED TYPE DATA FOR " + virtualPath + " == " + typeData);
	//			FieldData fieldData = new FieldData(field, name, setting, typeData);
	//			result.put(virtualPath, fieldData);
	//		});
	//		return result;
	//	}

	public void load(Class<?> configClass) throws IOException {
		OsmiumLogger.info("Loading config: " + configClass.getName());
		getConfigData(configClass).load();
		if (OsmiumConfig.class.isAssignableFrom(configClass)) {
			configInstances.computeIfAbsent(configClass, k -> Reflection.cast(Reflection.createInstance(configClass))).onLoad();
		}
	}

	public void save(Class<?> configClass) throws IOException {
		OsmiumLogger.info("Saving config: " + configClass.getName());
		getConfigData(configClass).save();
		if (OsmiumConfig.class.isAssignableFrom(configClass)) {
			configInstances.computeIfAbsent(configClass, k -> Reflection.cast(Reflection.createInstance(configClass))).onSave();
		}
	}

	private static HashMap<Class<?>, OsmiumConfig> configInstances = new HashMap<>();

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

	public static ClassTypeData getTypeData(Class<?> cls) {
		return globalTypeData.get(cls);
	}

	public static String getPhysicalPath(Field field, int truncate) {
		return getFullPath(field).substring(truncate); //Truncate must come last
	}

	public static String getFullPath(Field field) {
		return field.getDeclaringClass().getName() + "." + field.getName();
	}

	public static String getVirtualPath(Field field, String name) {
		Class<?> current = field.getDeclaringClass();
		ArrayList<String> list = new ArrayList<>(Arrays.asList(name));
		while (!current.isAnnotationPresent(ConfigClass.class) && !current.isAnnotationPresent(ConfigSerializable.class)) {
			list.add(0, normalizeName(current.getSimpleName()));
			current = current.getEnclosingClass();
		}

		//		(enclosingPath + "." + field.getName()).replace('$', '.').substring(truncate).toLowerCase();
		//		System.out.println("GET VIRTUAL PATH: " + String.join(".", list).toLowerCase());
		return String.join(".", list).toLowerCase();
	}

	public static String getName(Field field) {
		return getName(field, field.getAnnotation(Setting.class));
	}

	public static String getName(Field field, Setting setting) {
		if (setting == null || setting.name().isEmpty()) {
			return normalizeName(field.getName());
		} else {
			return setting.name();
		}
	}

	private static String normalizeName(String name) {
		return StringUtil.normalizeCamelCase(name, "-");
	}

}
