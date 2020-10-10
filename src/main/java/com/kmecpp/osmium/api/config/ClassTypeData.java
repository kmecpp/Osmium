package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;

public class ClassTypeData {

	private Class<?> cls;
	private HashMap<String, FieldTypeData> fieldTypeMap;

	public ClassTypeData(Class<?> cls, HashMap<String, FieldTypeData> fieldTypeMap) {
		this.cls = cls;
		this.fieldTypeMap = fieldTypeMap;
	}

	public FieldTypeData get(Field field) {
		int truncate = cls.getName().length() + 1;
		String physicalPath = ConfigManager.getPhysicalPath(field, truncate);

		FieldTypeData typeData = fieldTypeMap.get(physicalPath);
		if (typeData == null) {
			//This is normal if the type isn't supposed to have generics
			typeData = new FieldTypeData(field.getType(), Collections.emptyList());
		}
		return typeData;
	}

	public Class<?> getTargetClass() {
		return cls;
	}

	@Override
	public String toString() {
		return cls + ": " + String.valueOf(fieldTypeMap);
	}

}
