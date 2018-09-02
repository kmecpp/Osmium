package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;

public class ConfigField {

	private final String[] path;
	private final Field field;
	private final Setting setting;

	public ConfigField(String parent, Field field, Setting setting) {
		this.path = getFullPath(parent, setting, field).split("\\.");
		this.field = field;
		this.setting = setting;
	}

	public String[] getPath() {
		return path;
	}

	public Field getField() {
		return field;
	}

	public Setting getSetting() {
		return setting;
	}

	private String getFullPath(String parent, Setting setting, Field field) {

		if (!setting.name().isEmpty()) {
			return parent + "." + setting.name();
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < field.getName().length(); i++) {
			char c = field.getName().charAt(i);
			if (i > 0 && Character.isUpperCase(c)) {
				sb.append("-");
			}
			sb.append(Character.toLowerCase(c));
		}
		return parent + sb.toString();
	}

}
