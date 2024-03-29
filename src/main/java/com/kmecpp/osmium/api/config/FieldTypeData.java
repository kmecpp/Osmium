package com.kmecpp.osmium.api.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.util.Require;

public class FieldTypeData {

	private Class<?> type;
	private List<FieldTypeData> args;

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

	public FieldTypeData(Class<?> type, List<FieldTypeData> generics) {
		this.type = Require.nonNull(type);
		this.args = Require.nonNull(generics);

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

	public List<FieldTypeData> getGenerics() {
		return args;
	}

	public List<FieldTypeData> flattenArgs() {
		ArrayList<FieldTypeData> list = new ArrayList<>();
		for (FieldTypeData fieldTypeData : args) {
			list.add(fieldTypeData);
			list.addAll(fieldTypeData.flattenArgs());
		}
		return list;
	}

	public void walk(Consumer<FieldTypeData> f) {
		for (FieldTypeData typeData : args) {
			f.accept(typeData);
			typeData.walk(f);
		}
	}

	/*
	 * ArrayList<String>
	 * java.util.HashMap<java.lang.String,java.lang.String>
	 * java.util.HashMap<java.util.HashMap<java.lang.String,java.lang.String>,
	 * java.util.ArrayList<java.util.HashSet<java.lang.String>>>
	 */
	//java.util.HashMap<java.util.HashMap<java.lang.String,java.lang.String>,java.util.ArrayList<java.util.HashSet<java.lang.String>>>
	public static FieldTypeData parse(ClassLoader classLoader, String str) throws ClassNotFoundException {
		Stack<FieldTypeData> stack = new Stack<>();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == ' ') {
				continue; //There shouldn't be any strings in the actual string but this makes testing easier
			} else if (c == '<') {
				stack.push(new FieldTypeData(Class.forName(sb.toString(), false, classLoader), new ArrayList<>()));
				sb.setLength(0);
			} else if (c == '>') {
				FieldTypeData popped = stack.pop();
				//				System.out.println("POPPED: " + popped);

				if (sb.length() > 0) { //Only do this if we're at the end of a type (we might not be)
					Class<?> read = Class.forName(sb.toString(), false, classLoader);
					popped.args.add(new FieldTypeData(read, new ArrayList<>()));
					sb.setLength(0);
				}

				if (!stack.isEmpty()) {
					stack.peek().args.add(popped);
				} else {
					return popped;
				}
			} else if (c == ',') {
				if (sb.length() > 0) { //There may be a comma after a '>' in which case we're at the begging of a type not the end like here
					Class<?> read = Class.forName(sb.toString(), false, classLoader);
					stack.peek().args.add(new FieldTypeData(read, new ArrayList<>()));
					sb.setLength(0);
				}
			} else {
				sb.append(c);
			}
		}

		String typeName = sb.toString();
		Class<?> type = primitives.get(typeName);
		type = type != null ? type : Class.forName(typeName, false, classLoader); //Can't use getOrDefault because Class.forName() with throw and error

		return new FieldTypeData(type, Collections.emptyList()); //Simple type
	}

	/**
	 * This converts a value loaded from the config by Configurate (ex: Set
	 * (HashSet)) to its ACTUAL type as given in the class (ex: LinkedHashSet)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object convertToActualType(Object loadedValue, PluginConfigTypeData pluginData) {
		//		System.out.println("CONVERT TO " + type + " FROM " + (loadedValue != null ? loadedValue.getClass() : "") + " :: " + loadedValue);
		//		System.out.println(type.getPackage() == null ? "NULL" : type.getPackage().getName());

		if (loadedValue == null) {
			return ConfigSerialization.getDefaultFor(type, false);
		} else if (type.isPrimitive() || type.getName().startsWith("java.lang")) {
			if (type != String.class && loadedValue instanceof String) {
				return Serialization.deserialize(type, (String) loadedValue);
			} else if ((type == float.class || type == Float.class) && loadedValue instanceof Double) {
				return ((Double) loadedValue).floatValue(); //Decimals are loaded as double by default. Need this to convert to floats
			}
			return loadedValue;
		} else if (Collection.class.isAssignableFrom(loadedValue.getClass())) {
			Collection result = (Collection) ConfigSerialization.getDefaultFor(type, true); //Collections must have a default value because we initialize them to be empty

			for (Object o : (Collection) loadedValue) {
				if (args.size() >= 1) {
					result.add(args.get(0).convertToActualType(o, pluginData));
				}
			}

			return result;
		} else if (Map.class.isAssignableFrom(loadedValue.getClass())) {
			if (!Map.class.isAssignableFrom(type)) { //If we loaded a Map but the field is not a Map we probably used @ConfigSerializable
				return ConfigUtil.deserializeFromConfigurateMap((Map) loadedValue, this, pluginData);
				//				return ObjectMapSerialization.deserialize((Map) loadedValue, type);
			}

			Map result;
			if (this.type == EnumMap.class) { //Can't have a default value for EnumMap because it requires generic type info which we only have here
				result = new EnumMap<>((Class<Enum>) args.get(0).type);
			} else {
				result = (Map) ConfigSerialization.getDefaultFor(type, true); //Map must have a default value because we initialize them to be empty
			}

			for (Entry entry : (Set<Entry>) ((Map) loadedValue).entrySet()) {
				if (args.size() >= 2) {
					result.put(
							args.get(0).convertToActualType(entry.getKey(), pluginData),
							args.get(1).convertToActualType(entry.getValue(), pluginData));
				}
			}
			return result;
		} else if (loadedValue instanceof String) { //type can't be string because java.lang is handled already
			return Serialization.deserialize(type, (String) loadedValue);
		} else {
			throw new IllegalArgumentException("Don't know how to map type: " + this);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convertToConfigurateType(Object actualValue) {
		//		System.out.println("CONVERT TO CONFIGURATE TYPE: " + actualValue);
		//		System.out.println(actualValue);
		//		if (actualValue != null) {
		//			System.out.println(actualValue.getClass());
		//			System.out.println(actualValue.getClass().getName());
		//			System.out.println(actualValue.getClass().getPackage());
		//			System.out.println(actualValue.getClass().getPackage().getName());
		//		}
		if (actualValue == null || actualValue.getClass().getName().startsWith("java.lang")) {
			return actualValue;
		} else if (actualValue instanceof Collection) {
			ArrayList converted = new ArrayList();

			for (Object obj : (Collection) actualValue) {
				converted.add(args.get(0).convertToConfigurateType(obj));
			}
			return converted;
		} else if (actualValue instanceof Map) {
			HashMap converted = new HashMap();

			for (Entry entry : (Set<Entry>) ((Map) actualValue).entrySet()) {
				converted.put(args.get(0).convertToConfigurateType(entry.getKey()), args.get(1).convertToConfigurateType(entry.getValue()));
			}
			return converted;
		} else if (Serialization.isSerializable(actualValue.getClass())) {
			return Serialization.serialize(actualValue);
		} else {
			return ConfigUtil.serializeAsConfigurateMap(actualValue, this);
			//			return ObjectMapSerialization.serialize(actualValue);
		}
	}

	public boolean isPrimitive() {
		return type.isPrimitive();
	}

	@Override
	public String toString() {
		return type.getSimpleName() + (args.isEmpty() ? "" : "<" + args.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ">");
	}

}
