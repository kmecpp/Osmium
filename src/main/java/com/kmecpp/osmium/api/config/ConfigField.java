package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;

public class ConfigField {

	private final String[] path;
	private final Field field;
	private final Setting setting;

	public ConfigField(String path, Field field, Setting setting) {
		this.path = getFullPath(path, setting, field).split("\\.");
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

	private String getFullPath(String path, Setting setting, Field field) {
		String parent = path.isEmpty() ? path : path + ".";

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
		return sb.toString();
	}

}
