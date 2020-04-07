package com.kmecpp.osmium.api.config;

import java.util.HashMap;
import java.util.Map.Entry;

public class PluginConfigTypeData {

	private HashMap<Class<?>, HashMap<String, TypeData>> data = new HashMap<>(); //<ConfigClass, <FieldPath, TypeData>>
	private HashMap<String, HashMap<String, String>> parsed = new HashMap<>(); //<ConfigClass, <FieldPath, TypeData>>

	private PluginConfigTypeData() {
	}

	public HashMap<String, TypeData> get(Class<?> cls) throws ClassNotFoundException {
		HashMap<String, TypeData> result = data.get(cls);
		if (result == null) {
			String className = cls.getName();
			HashMap<String, String> text = parsed.get(className);
			if (text == null) {
				throw new IllegalArgumentException("Missing config type data for class: " + className);
			}
			result = new HashMap<>();
			for (Entry<String, String> entry : text.entrySet()) {
				result.put(entry.getKey(), TypeData.parse(entry.getValue()));
			}
			data.put(cls, result);
		}
		return result;
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
				if (currentConfig == null || !line.startsWith(currentConfig)) {
					currentConfig = line.substring(0, line.length() - 1);
					data.parsed.put(currentConfig, current);
					current = new HashMap<>();
				}
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
