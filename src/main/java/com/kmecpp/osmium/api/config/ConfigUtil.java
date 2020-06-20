package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.kmecpp.osmium.api.Transient;
import com.kmecpp.osmium.api.util.Reflection;

public class ConfigUtil {

	public static HashMap<String, Object> serializeAsConfigurateMap(Object object, TypeData typeData) {
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
				result.put(field.getName(), typeData.convertToConfigurateType(field.get(object)));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object deserializeFromConfigurateMap(Map<String, Object> map, TypeData typeData, PluginConfigTypeData pluginData) {
		//		System.out.println("DESERIALIZING FROM MAP : " + typeData.getType());
		try {
			Object result = Reflection.createInstance(typeData.getType());
			for (Field field : typeData.getType().getDeclaredFields()) {
				if (field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				field.setAccessible(true);

				ClassTypeData classTypeData = pluginData.getForConfigClass(typeData.getType());
				//				System.out.println("CLASS TYPE DATA: " + classTypeData);
				TypeData fieldTypeData = classTypeData.get(field);

				field.set(result, fieldTypeData.convertToActualType(map.get(field.getName()), pluginData));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
