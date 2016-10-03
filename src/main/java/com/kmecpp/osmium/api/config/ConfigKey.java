package com.kmecpp.osmium.api.config;

import com.kmecpp.jlib.object.ObjectValue;

public class ConfigKey {

	private final String key;
	private final Object[] path;
	private final Object defaultValue;
	private final String comment;

	private ObjectValue value = ObjectValue.NULL;

	public ConfigKey(String key) {
		this(key, "");
	}

	public ConfigKey(String key, Object defaultValue) {
		this(key, defaultValue, "");
	}

	public ConfigKey(String key, Object defaultValue, String comment) {
		this.key = key;
		this.path = key.split("\\.");
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

	public Object[] getPath() {
		return path;
	}

	public String getKey() {
		return key;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getComment() {
		return comment;
	}

	public ObjectValue getValue() {
		return value;
	}

	public void setValue(Object value) {
		setValue(value, false);
	}

	public void setValue(Object value, boolean saveConfig) {
		this.value = ObjectValue.of(value);
		ConfigManager.setValue(this, value);

		if (saveConfig) {
			ConfigManager.save();
		}
	}

}
