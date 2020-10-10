package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.kmecpp.osmium.api.Transient;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.Reflection;

public class ConfigUtil {

	public static HashMap<String, Object> serializeAsConfigurateMap(Object object, FieldTypeData typeData) {
		HashMap<String, Object> result = new HashMap<>();
		if (object == null) {
			return result;
		}
		ClassTypeData classTypeData = ConfigManager.getTypeData(object.getClass());
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				//				System.out.println("SERIALIZING: " + classTypeData.getTargetClass().getName());
				field.setAccessible(true);
				FieldTypeData fieldTypeData = classTypeData.get(field);
				result.put(field.getName(), fieldTypeData.convertToConfigurateType(field.get(object)));
			}
			return result;
		} catch (Exception e) {
			OsmiumLogger.warn("An error occurred while trying to serialize " + object.getClass().getName() + " to the following type: " + typeData);
			throw new RuntimeException(e);
		}
	}

	public static Object deserializeFromConfigurateMap(Map<String, Object> map, FieldTypeData typeData, PluginConfigTypeData pluginData) {
		//		System.out.println("DESERIALIZING FROM MAP : " + typeData.getType());
		try {
			Object result = Reflection.createInstance(typeData.getType());
			for (Field field : typeData.getType().getDeclaredFields()) {
				if (field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				field.setAccessible(true);

				HashMap<Class<?>, ClassTypeData> classTypeData = pluginData.getForConfigClass(typeData.getType());
				//				System.out.println("CLASS TYPE DATA: " + classTypeData);
				FieldTypeData fieldTypeData = classTypeData.get(typeData.getType()).get(field);

				field.set(result, fieldTypeData.convertToActualType(map.get(field.getName()), pluginData));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
