package com.kmecpp.osmium.api.persistence;

import java.lang.reflect.Field;

public class PersistentField {

	private Persistent annotation;
	private Field field;

	public PersistentField(Persistent annotation, Field field) {
		this.annotation = annotation;
		this.field = field;
	}

	public Persistent getAnnotation() {
		return annotation;
	}

	public Field getField() {
		return field;
	}

	public String getId() {
		return annotation.id();
	}

	public Object getValue() {
		try {
			return field.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public String getLocation() {
		return field.getDeclaringClass().getName() + "." + field.getName();
	}

}
