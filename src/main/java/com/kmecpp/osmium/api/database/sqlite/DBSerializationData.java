package com.kmecpp.osmium.api.database.sqlite;
//package com.kmecpp.osmium.api.database;
//
//import com.kmecpp.osmium.api.persistence.Deserializer;
//import com.kmecpp.osmium.api.persistence.Serializer;
//
//public class DBSerializationData<T> {
//
//	private final DBType type;
//	private final Class<T> serializableClass;
//	private final Serializer<T> serializer;
//	private final Deserializer<T> deserializer;
//
//	public DBSerializationData(Class<T> cls, Deserializer<T> deserializer) {
//		this(DBType.SERIALIZABLE, cls, null, deserializer);
//	}
//
//	public DBSerializationData(Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
//		this(DBType.SERIALIZABLE, cls, serializer, deserializer);
//	}
//
//	public DBSerializationData(DBType type, Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
//		this.type = type;
//		this.serializableClass = cls;
//		this.serializer = serializer;
//		this.deserializer = deserializer;
//	}
//
//	public DBType getType() {
//		return type;
//	}
//
//	public Class<T> getSerializableClass() {
//		return serializableClass;
//	}
//
//	public String serialize(T obj) {
//		return serializer != null ? serializer.serialize(obj) : String.valueOf(obj);
//	}
//
//	public T deserialize(String str) {
//		return deserializer.deserialize(str);
//		//		return deserializer != null ? deserializer.deserialize(str) : cls.getConstructor;
//	}
//
//}
