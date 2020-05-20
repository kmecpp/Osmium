package com.kmecpp.osmium.api.persistence;

import java.util.HashMap;

import com.kmecpp.osmium.api.database.DBType;

public class DatabaseSerialization {

	private static final HashMap<Class<?>, SerializationData<?>> database = new HashMap<>();

	static {
		//		register(boolean.class, DBType.BOOLEAN, b -> b ? 1 : 0, deserializer);
		//		register(Boolean.class, DBType.BOOLEAN, b -> b ? 1 : 0, );
	}

	public static <T> void register(Class<T> cls, DBType type, Serializer<T> serializer, Deserializer<T> deserializer) {
		database.put(cls, new SerializationData<>(type, true, serializer, deserializer));
	}

	public static <T> String serialize(T obj) {
		if (obj == null) {
			return "null";
		}

		@SuppressWarnings("unchecked")
		SerializationData<T> data = (SerializationData<T>) Serialization.getData(obj.getClass());
		if (data != null) {
			return data.isCustomType() ? "\"" + data.serialize(obj) + "\"" : data.serialize(obj);
		} else {
			return String.valueOf(obj);
		}
	}

}
