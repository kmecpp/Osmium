package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.util.Reflection;

public class TypeData {

	private Class<?> type;
	private List<TypeData> generics;

	//	private TypeToken<?> typeToken;

	private static final HashMap<String, Class<?>> primitives = new HashMap<>();

	static {
		primitives.put("byte", byte.class);
		primitives.put("short", short.class);
		primitives.put("int", int.class);
		primitives.put("long", long.class);
		primitives.put("float", float.class);
		primitives.put("double", double.class);
		primitives.put("boolean", boolean.class);
		primitives.put("char", char.class);
	}

	public TypeData(Class<?> type, List<TypeData> generics) {
		this.type = type;
		this.generics = generics;

		//		if (!ConfigSerialization.isConfigurateSerializable(type)) {
		//			TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(type), new TypeSerializer() {
		//
		//				@Override
		//				public Object deserialize(@NonNull TypeToken type, @NonNull ConfigurationNode value) throws ObjectMappingException {
		//					return null;
		//				}
		//
		//				@Override
		//				public void serialize(@NonNull TypeToken type, Object obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
		//				}
		//			});
		//		}
	}

	public Class<?> getType() {
		return type;
	}

	//	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	//	public TypeToken<?> getTypeToken() {
	//		if (typeToken == null) {
	//			TypeToken result = new TypeToken<Object>(type) {};
	//			for (TypeData type : generics) {
	//				System.out.println();
	//				result = result.where(new TypeParameter<Object>() {}, type.getTypeToken());
	//			}
	//			return result;
	//		}
	//		return typeToken;
	//	}

	public List<TypeData> getGenerics() {
		return generics;
	}

	/*
	 * ArrayList<String>
	 * java.util.HashMap<java.lang.String,java.lang.String>
	 * java.util.HashMap<java.util.HashMap<java.lang.String,java.lang.String>,
	 * java.util.ArrayList<java.util.HashSet<java.lang.String>>>
	 */
	//java.util.HashMap<java.util.HashMap<java.lang.String,java.lang.String>,java.util.ArrayList<java.util.HashSet<java.lang.String>>>
	public static TypeData parse(String str) throws ClassNotFoundException {
		Stack<TypeData> stack = new Stack<>();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == ' ') {
				continue; //There shouldn't be any strings in the actual string but this makes testing easier
			} else if (c == '<') {
				stack.push(new TypeData(Class.forName(sb.toString()), new ArrayList<>()));
				sb.setLength(0);
			} else if (c == '>') {
				TypeData popped = stack.pop();
				//				System.out.println("POPPED: " + popped);

				if (sb.length() > 0) { //Only do this if we're at the end of a type (we might not be)
					Class<?> read = Class.forName(sb.toString());
					popped.generics.add(new TypeData(read, new ArrayList<>()));
					sb.setLength(0);
				}

				if (!stack.isEmpty()) {
					stack.peek().generics.add(popped);
				} else {
					return popped;
				}
			} else if (c == ',') {
				if (sb.length() > 0) { //There may be a comma after a '>' in which case we're at the begging of a type not the end like here
					Class<?> read = Class.forName(sb.toString());
					stack.peek().generics.add(new TypeData(read, new ArrayList<>()));
					sb.setLength(0);
				}
			} else {
				sb.append(c);
			}
		}

		String typeName = sb.toString();
		Class<?> type = primitives.get(typeName);
		type = type != null ? type : Class.forName(typeName); //Can't use getOrDefault because Class.forName() with throw and error
		return new TypeData(type, Collections.emptyList()); //Simple type
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object convertToActualType(Object loadedValue) {
		System.out.println("CONVERT TO " + type.getName() + ": " + loadedValue + " :: " + (loadedValue != null ? loadedValue.getClass() : ""));
		System.out.println(type.getPackage() == null ? "NULL" : type.getPackage().getName());

		if (loadedValue == null) {
			return ConfigSerialization.getDefaultFor(type);
		} else if (type.isPrimitive() || type.getPackage().getName().startsWith("java.lang")) {
			return loadedValue;
		} else if (Collection.class.isAssignableFrom(loadedValue.getClass())) {
			ArrayList convertedList = new ArrayList<>();
			for (Object o : (Collection) loadedValue) {
				if (generics.size() >= 1) {
					convertedList.add(generics.get(0).convertToActualType(o));
				}
			}
			Collection result = (Collection) ConfigSerialization.getDefaultFor(type);
			result.addAll(convertedList);
			return result;
		} else if (Map.class.isAssignableFrom(loadedValue.getClass())) {
			if (!Map.class.isAssignableFrom(type)) { //If the original type was not a Map we did our own map serialization on it
				this.deserializeFromConfigurateMap((Map) loadedValue);
				//				return ObjectMapSerialization.deserialize((Map) loadedValue, type);
			}

			HashMap convertedMap = new HashMap();

			for (Entry entry : (Set<Entry>) ((Map) loadedValue).entrySet()) {
				if (generics.size() >= 2) {
					convertedMap.put(generics.get(0).convertToActualType(entry.getKey()), generics.get(1).convertToActualType(entry.getValue()));
				}
			}
			Map result = (Map) ConfigSerialization.getDefaultFor(type);
			result.putAll(convertedMap);
			return result;
		} else if (loadedValue instanceof String) { //type can't be string because java.lang is handled already
			return Serialization.deserialize(type, (String) loadedValue);
		} else {
			throw new IllegalArgumentException("Don't know how to map type: " + this);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convertToConfigurateType(Object actualValue) {
		if (actualValue == null || actualValue.getClass().getPackage().getName().startsWith("java.lang")) {
			return actualValue;
		} else if (actualValue instanceof Collection) {
			ArrayList converted = new ArrayList();

			for (Object obj : (Collection) actualValue) {
				converted.add(generics.get(0).convertToConfigurateType(obj));
			}
			return converted;
		} else if (actualValue instanceof Map) {
			HashMap converted = new HashMap();

			for (Entry entry : (Set<Entry>) ((Map) actualValue).entrySet()) {
				converted.put(generics.get(0).convertToConfigurateType(entry.getKey()), generics.get(1).convertToConfigurateType(entry.getValue()));
			}
			return converted;
		} else if (Serialization.isSerializable(actualValue.getClass())) {
			return Serialization.serialize(actualValue);
		} else {
			return serializeAsConfigurateMap(actualValue);
			//			return ObjectMapSerialization.serialize(actualValue);
		}
	}

	public HashMap<String, Object> serializeAsConfigurateMap(Object object) {
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
				result.put(field.getName(), convertToConfigurateType(field.get(object)));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object deserializeFromConfigurateMap(Map<String, Object> map) {
		System.out.println("DESERIALIZING FROM MAP : " + type);
		try {
			Object result = Reflection.createInstance(type);
			for (Field field : type.getDeclaredFields()) {
				if (field.isAnnotationPresent(Transient.class)) {
					continue;
				}
				field.setAccessible(true);
				
				//TODO: This should be getType(field).convertToActual(map.get()))
				field.set(result, convertToActualType(map.get(field.getName())));
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isPrimitive() {
		return type.isPrimitive();
	}

	@Override
	public String toString() {
		return type.getSimpleName() + (generics.isEmpty() ? "" : "<" + generics.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ">");
	}

}
