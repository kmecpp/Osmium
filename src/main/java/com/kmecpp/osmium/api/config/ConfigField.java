package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class ConfigField {

	private final Field field;
	private final Setting setting;

	public ConfigField(Field field, Setting setting) {
		this.field = field;
		this.setting = setting;
	}

	public Setting getSetting() {
		return setting;
	}

	public String getName() {
		return setting.name().isEmpty() ? field.getName() : setting.name();
	}

	public boolean isPrimitive() {
		return field.getType().isPrimitive();
	}

	public boolean isArray() {
		return field.getType().isArray();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public Class<?> getComponentType() {
		if (field.getType().isArray()) {
			return field.getType().getComponentType();
		} else if (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())) {
			return setting.type();
		}
		return field.getType();
	}

	public void setValue(Object value) {
		try {
			field.set(null, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getValue() {
		try {
			return field.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
