package com.kmecpp.osmium.api.config;

import java.util.HashMap;

public class ConfigData {

	private Block root;
	private Class<?> configClass;
	private ConfigProperties properties;

	private HashMap<String, ConfigField> fields;

	public ConfigData(Class<?> configClass, ConfigProperties properties) {
		this.root = new Block("root", 0, "");
		this.configClass = configClass;
		this.properties = properties;
		this.fields = new HashMap<>();
	}

	public Block getRoot() {
		return root;
	}

	public Class<?> getConfigClass() {
		return configClass;
	}

	public ConfigProperties getProperties() {
		return properties;
	}

	public ConfigField getField(String path) {
		return fields.get(path);
	}

	public HashMap<String, ConfigField> getFields() {
		return fields;
	}

	//	public HashMap<String, Object> getValues() {
	//		if (values == null) {
	//			values = new HashMap<>();
	//			for (Entry<String, ConfigField> entry : fields.entrySet()) {
	//				values.put(entry.getKey(), entry.getValue().getValue());
	//			}
	//		}
	//		return values;
	//	}

}
