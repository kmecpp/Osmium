package com.kmecpp.osmium.api.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class PluginConfigTypeData {

	private final ClassLoader classLoader;

	private final HashMap<String, HashMap<String, String>> unloadedData = new HashMap<>(); //<ConfigClass, <FieldPath, TypeData>>
	private final HashMap<Class<?>, HashMap<String, FieldTypeData>> loadedData = new HashMap<>(); //<ConfigClass, <FieldPath, TypeData>>

	public PluginConfigTypeData(@Nullable OsmiumPlugin plugin) {
		this.classLoader = plugin != null ? plugin.getClass().getClassLoader() : this.getClass().getClassLoader();
	}

	public HashMap<Class<?>, ClassTypeData> getForConfigClass(Class<?> cls) throws ClassNotFoundException {
		HashMap<Class<?>, ClassTypeData> result = new HashMap<>();
		HashMap<String, FieldTypeData> fieldTypeMap = loadedData.get(cls);
		if (fieldTypeMap == null) {
			String className = cls.getName(); //Full path
			HashMap<String, String> text = unloadedData.get(className);
			if (text == null) {
				throw new IllegalArgumentException("Missing config type data for class: " + className);
			}

			fieldTypeMap = new HashMap<>();
			//			ConfigManager.getVirtualPath(enclosingPath, name, truncate)
			for (Entry<String, String> entry : text.entrySet()) {
				FieldTypeData typeData = FieldTypeData.parse(classLoader, entry.getValue());
				fieldTypeMap.put(entry.getKey(), typeData);

				//Visit all sub types
				HashMap<String, FieldTypeData> subTypes = new HashMap<>();
				for (FieldTypeData subTypeData : typeData.flattenArgs()) {
					if (subTypeData.getType().isAnnotationPresent(ConfigSerializable.class)) {
						result.putAll(getForConfigClass(subTypeData.getType()));
						//						try {
						//							String name = subTypeData.getType().getName();
						//							System.out.println("TYPE NAME: " + name);
						//							FieldTypeData configSerializableTypeData = FieldTypeData.parse(name);
						//							if (configSerializableTypeData != null) {
						//								OsmiumLogger.warn("Putting " + name + " AS " + configSerializableTypeData);
						//								subTypes.put(name, configSerializableTypeData);
						//							} else {
						//								OsmiumLogger.warn("@" + ConfigSerializable.class.getSimpleName() + " " + name + " class type data not found in CONFIG_TYPES resource");
						//							}
						//						} catch (ClassNotFoundException e) {
						//							e.printStackTrace();
						//						}
					}
				}
				fieldTypeMap.putAll(subTypes);

			}
			loadedData.put(cls, fieldTypeMap);
		}

		//		for (Entry<Class<?>, HashMap<String, FieldTypeData>> e1 : loadedData.entrySet()) {
		//			System.out.println(e1.getKey().getName());
		//			for (Entry<String, FieldTypeData> e2 : e1.getValue().entrySet()) {
		//				System.out.println("  " + e2.getKey() + ": " + e2.getValue());
		//			}
		//			System.out.println("----");
		//		}
		result.put(cls, new ClassTypeData(cls, fieldTypeMap));
		return result;
	}

	public static PluginConfigTypeData parse(OsmiumPlugin plugin, List<String> configTypesFile) {
		PluginConfigTypeData data = new PluginConfigTypeData(plugin);

		HashMap<String, String> current = new HashMap<>();
		String currentConfig = null;
		for (String line : configTypesFile) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.endsWith(":")) {
				if (currentConfig != null) {
					data.unloadedData.put(currentConfig, current);
					current = new HashMap<>();
				}
				currentConfig = line.substring(0, line.length() - 1);
				continue;
			} else {
				String[] parts = line.split("=");
				current.put(parts[0].substring(currentConfig.length() + 1), parts[1]);
			}
		}
		data.unloadedData.put(currentConfig, current);

		return data;
	}

}
