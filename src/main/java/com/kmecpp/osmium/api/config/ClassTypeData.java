package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;

public class ClassTypeData {

	private Class<?> rootClass;
	private HashMap<String, TypeData> fieldTypeMap;

	public ClassTypeData(Class<?> rootClass, HashMap<String, TypeData> fieldTypeMap) {
		this.rootClass = rootClass;
		this.fieldTypeMap = fieldTypeMap;
	}

	public TypeData get(Field field) {
		int truncate = rootClass.getName().length() + 1;
		String physicalPath = ConfigManager.getPhysicalPath(field, truncate);

		TypeData typeData = fieldTypeMap.get(physicalPath);
		if (typeData == null) {
			//This is normal if the type isn't supposed to have generics
			typeData = new TypeData(field.getType(), Collections.emptyList());
		}
		return typeData;
	}

	@Override
	public String toString() {
		return rootClass + ": " + String.valueOf(fieldTypeMap);
	}

}
