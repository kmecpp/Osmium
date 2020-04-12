package com.kmecpp.osmium.api.config;

import java.util.HashMap;
import java.util.Map.Entry;

public class PluginConfigTypeData {

	private HashMap<Class<?>, HashMap<String, TypeData>> data = new HashMap<>(); //<ConfigClass, <FieldPath, TypeData>>
	private HashMap<String, HashMap<String, String>> parsed = new HashMap<>(); //<ConfigClass, <FieldPath, TypeData>>

	private PluginConfigTypeData() {
	}

	public ClassTypeData getForConfigClass(Class<?> cls) throws ClassNotFoundException {
		HashMap<String, TypeData> result = data.get(cls);
		if (result == null) {
			String className = cls.getName(); //Full path
			HashMap<String, String> text = parsed.get(className);
			if (text == null) {
				throw new IllegalArgumentException("Missing config type data for class: " + className);
			}

			result = new HashMap<>();
			//			ConfigManager.getVirtualPath(enclosingPath, name, truncate)
			for (Entry<String, String> entry : text.entrySet()) {
				TypeData typeData = TypeData.parse(entry.getValue());
				result.put(entry.getKey(), typeData);

				//Visit all sub types
				//				typeData.walk(data -> {
				//					if (data.getType().isAnnotationPresent(ConfigSerializable.class)) {
				//						String name = typeData.getType().getName();
				//						TypeData configSerializableTypeData = TypeData.parse(name);
				//						if (configSerializableTypeData != null) {
				//							result.put(name, configSerializableTypeData);
				//						} else {
				//							OsmiumLogger.warn("@" + ConfigSerializable.class.getSimpleName() + " " + name + " class type data not found in CONFIG_TYPES resource");
				//						}
				//					}
				//				});

			}
			data.put(cls, result);
		}
		return new ClassTypeData(cls, result);
	}

	public static PluginConfigTypeData parse(String[] configTypesFile) {
		PluginConfigTypeData data = new PluginConfigTypeData();

		HashMap<String, String> current = new HashMap<>();
		String currentConfig = null;
		for (String line : configTypesFile) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.endsWith(":")) {
				if (currentConfig != null) {
					data.parsed.put(currentConfig, current);
					current = new HashMap<>();
				}
				currentConfig = line.substring(0, line.length() - 1);
				continue;
			} else {
				String[] parts = line.split("=");
				current.put(parts[0].substring(currentConfig.length() + 1), parts[1]);
			}
		}
		data.parsed.put(currentConfig, current);

		return data;
	}

}
