package com.kmecpp.osmium.api.config;

import java.util.HashMap;

public class ConfigClassData {

	protected final PluginConfigTypeData pluginData;
	protected final Class<?> configClass;
	protected final ConfigClass properties;
	protected final HashMap<String, FieldData> fieldData;

	protected ConfigClassData(PluginConfigTypeData pluginData, Class<?> configClass, ConfigClass configProperties, HashMap<String, FieldData> fieldData) {
		this.pluginData = pluginData;
		this.configClass = configClass;
		this.properties = configProperties;
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

	public Class<?> getConfigClass() {
		return configClass;
	}

	public ConfigClass getConfigProperties() {
		return properties;
	}

}
