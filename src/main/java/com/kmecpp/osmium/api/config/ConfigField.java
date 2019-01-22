package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.ArrayList;

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

	public String getJavaPath() {
		return field.getDeclaringClass().getName() + "." + field.getName();
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

	public Class<?>[] getComponentTypes() {
		if (field.getType().isArray()) {
			ArrayList<Class<?>> componentTypes = new ArrayList<>();
			Class<?> type = field.getType();
			//			componentTypes.add(type);
			while (type.isArray()) {
				type = type.getComponentType();
				componentTypes.add(type);
			}
			return componentTypes.toArray(new Class[componentTypes.size()]);
		}
		return setting.type();

		//		else if (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())) {
		//			//			Class<?>[] types = new Class[setting.type().length + 1];
		//			//			types[0] = field.getType();
		//			//			System.arraycopy(setting.type(), 0, types, 1, setting.type().length);
		//			//			return types;
		//			return setting.type();
		//		}
		//		return new Class[0];//new Class[] { field.getType() };
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
			Object value = field.get(null);
			if (value == null) {
				value = field.getType().newInstance();
				field.set(null, value);
				return value;
			}
			return field.get(null);
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
