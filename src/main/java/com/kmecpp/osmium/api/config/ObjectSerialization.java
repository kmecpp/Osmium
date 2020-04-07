package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.kmecpp.osmium.api.util.Reflection;

public class ObjectSerialization {

	public static HashMap<String, Object> serialize(Object object) {
		HashMap<String, Object> result = new HashMap<>();
		if (object == null) {
			return result;
		}
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				field.setAccessible(true);
				result.put(field.getName(), field.get(object));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object deserialize(Map<String, Object> map, Class<?> cls) {
		try {
			Object result = Reflection.createInstance(cls);
			for (Field field : cls.getDeclaredFields()) {
				if (field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				field.setAccessible(true);
				field.set(result, map.get(field.getName()));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
