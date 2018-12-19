package com.kmecpp.osmium.api.config;

import java.util.HashMap;
import java.util.UUID;

import com.kmecpp.osmium.api.persistence.Deserializer;
import com.kmecpp.osmium.api.persistence.SerializationData;
import com.kmecpp.osmium.api.persistence.Serializer;

public class ConfigTypes {

	private static final HashMap<Class<?>, SerializationData<?>> types = new HashMap<>();

	static {
		registerDefaultType(byte.class, Byte::parseByte);
		registerDefaultType(short.class, Short::parseShort);
		registerDefaultType(int.class, Integer::parseInt);
		registerDefaultType(long.class, Long::parseLong);
		registerDefaultType(float.class, Float::parseFloat);
		registerDefaultType(double.class, Double::parseDouble);
		registerDefaultType(boolean.class, Boolean::parseBoolean);

		registerDefaultType(Byte.class, Byte::parseByte);
		registerDefaultType(Short.class, Short::parseShort);
		registerDefaultType(Integer.class, Integer::parseInt);
		registerDefaultType(Long.class, Long::parseLong);
		registerDefaultType(Float.class, Float::parseFloat);
		registerDefaultType(Double.class, Double::parseDouble);
		registerDefaultType(Boolean.class, Boolean::parseBoolean);

		registerDefaultType(char.class, (s) -> s.charAt(0));
		registerDefaultType(String.class, (obj) -> obj == null ? null : "\"" + obj + "\"", String::valueOf);

		//		register(int[].class, Arrays::toString, DefaultTypes::getInts);
		//		register(long[].class, Arrays::toString, DefaultTypes::getInts);
		//		register(byte[].class, Arrays::toString, DefaultTypes::getInts);
		//		register(short[].class, Arrays::toString, DefaultTypes::getInts);
		//		register(float[].class, Arrays::toString, DefaultTypes::getInts);
		//		register(double[].class, Arrays::toString, DefaultTypes::getInts);
		//		register(boolean[].class, Arrays::toString, DefaultTypes::getInts);
		registerDefaultType(UUID.class, UUID::fromString);
	}

	private static <T> void registerDefaultType(Class<T> cls, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(false, String::valueOf, deserializer));
	}

	private static <T> void registerDefaultType(Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(false, serializer, deserializer));
	}

	public static <T> void register(Class<T> cls, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(true, String::valueOf, deserializer));
	}

	public static <T> void register(Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(true, serializer, deserializer));
	}

	@SuppressWarnings("unchecked")
	public static <T> String serialize(T obj) {
		SerializationData<T> data = (SerializationData<T>) types.get(obj.getClass());
		if (data != null) {
			return data.isCustomType() ? "\"" + data.serialize(obj) + "\"" : data.serialize(obj);
		}
		throw new IllegalArgumentException("Cannot serialize unknown class: " + obj.getClass().getName());
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Class<T> type, String str) {
		SerializationData<T> data = (SerializationData<T>) types.get(type);
		if (data != null) {
			return data.deserialize(data.isCustomType() ? str.substring(1, str.length() - 1) : str);
		}
		throw new IllegalArgumentException("Cannot parse as " + type.getName() + ": '" + str + "'");
	}

}
