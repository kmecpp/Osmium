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

	private void init() throws Exception {
		if (field == null) {
			field = cls.getDeclaredField(name);
			field.setAccessible(true);
		}
	}

	@SuppressWarnings("unchecked")
	public T get(Object instance) throws Exception {
		init();
		return (T) field.get(instance);
	}

	@SuppressWarnings("unchecked")
	public T getStatic() throws Exception {
		init();
		return (T) field.get(null);
	}

	public void set(Object instance, T value) throws Exception {
		init();
		field.set(instance, value);
	}

	public void setStatic(T value) throws Exception {
		init();
		field.set(null, value);
	}

}
