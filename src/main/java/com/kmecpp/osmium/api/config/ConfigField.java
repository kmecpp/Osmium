package com.kmecpp.osmium.api.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class ConfigField {

	private final Field field;
	private final Setting setting;

	public ConfigField(Field field) {
		this(field, DEFAULT_SETTING);
	}

	public ConfigField(Field field, Setting setting) {
		this.field = field;
		this.setting = setting != null ? setting : DEFAULT_SETTING;

		//		if (setting != null) {
		//			this.name = setting.name();
		//			this.comment = setting.comment();
		//			this.deletable = setting.deletable();
		//			this.types = setting.types();
		//		} else {
		//			this.name = "";
		//		}
	}

	public Setting getSetting() {
		return setting;
	}

	public String getName() {
		return setting != null && setting.name().isEmpty() ? field.getName() : setting.name();
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

	public Field getBackingField() {
		return field;
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
		return setting != null ? setting.type() : new Class<?>[0];

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
		return getValue(null);
	}

	public Object getValue(Object fieldInstance) {
		try {
			Object value = field.get(fieldInstance);
			if (value == null) {
				value = field.getType().newInstance();
				field.set(fieldInstance, value);
				return value;
			}
			return value;
		} catch (InstantiationException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final Setting DEFAULT_SETTING = new Setting() {

		@Override
		public Class<? extends Annotation> annotationType() {
			return Setting.class;
		}

		@Override
		public Class<?>[] type() {
			return new Class<?>[0];
		}

		@Override
		public String name() {
			return "";
		}

		@Override
		public String comment() {
			return "";
		}

		@Override
		public boolean deletable() {
			return false;
		}

	};

	@Override
	public String toString() {
		return "field[" + getName() + "]";
	}

}
