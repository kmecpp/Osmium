package com.kmecpp.osmium.api.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ConfigUtil {

	public static ConfigClassData generateConfigData(Class<?> configClass) {
		ConfigClass properties = configClass.getDeclaredAnnotation(ConfigClass.class);
		Path path = Paths.get(properties.path());
		HashMap<String, FieldData> fieldData = new HashMap<>();
		return new ConfigClassData(configClass, properties, path, fieldData);
	}

}
