package com.kmecpp.osmium.api.util;

import java.lang.reflect.Field;

public class ReflectField<T> {

	private final Class<?> cls;
	private final String name;

	private Field field;

	public ReflectField(Class<?> cls, String name) {
		this.cls = cls;
		this.name = name;
	}

	public ReflectField(String className, String name) {
		this(getClass(className), name);
	}

	private static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void init() throws Exception {
		if (field == null) {
			field = cls.getDeclaredField(name);
			field.setAccessible(true);
		}
	}

	@SuppressWarnings("unchecked")
	public T get(Object instance) {
		try {
			init();
			return (T) field.get(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public T getStatic() {
		try {
			init();
			return (T) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void set(Object instance, T value) {
		try {
			init();
			field.set(instance, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setStatic(T value) {
		try {
			init();
			field.set(null, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
