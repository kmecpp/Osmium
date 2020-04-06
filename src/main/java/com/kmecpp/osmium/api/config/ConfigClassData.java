package com.kmecpp.osmium.api.config;

import java.nio.file.Path;
import java.util.HashMap;

public class ConfigClassData {

	protected final Class<?> configClass;
	protected final ConfigClass properties;
	protected final Path path;
	protected final HashMap<String, FieldData> fieldData;

	protected ConfigClassData(Class<?> configClass, ConfigClass configProperties, Path path, HashMap<String, FieldData> fieldData) {
		this.configClass = configClass;
		this.properties = configProperties;
		this.path = path;
		this.fieldData = fieldData;
	}

	public boolean isLoadLate() {
		return properties.loadLate();
	}

	public boolean isManualLoad() {
		return properties.manualLoad();
	}

	public HashMap<String, FieldData> getFieldData() {
		return fieldData;
	}

	public Path getPath() {
		return path;
	}

	public Class<?> getConfigClass() {
		return configClass;
	}

	public ConfigClass getConfigProperties() {
		return properties;
	}

}
