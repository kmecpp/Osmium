package com.kmecpp.osmium.api.persistence;

import com.kmecpp.osmium.api.database.DBType;

public class SerializationData<T> {

	private DBType type;
	private boolean customType;
	private Serializer<T> serializer;
	private Deserializer<T> deserializer;

	public SerializationData(boolean customType, Serializer<T> serializer, Deserializer<T> deserializer) {
		this(DBType.SERIALIZABLE, customType, serializer, deserializer);
	}

	public SerializationData(DBType type, boolean customType, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.type = type;
		this.customType = customType;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	public Serializer<T> getSerializer() {
		return serializer;
	}

	public Deserializer<T> getDeserializer() {
		return deserializer;
	}

	public boolean isCustomType() {
		return customType;
	}

	public DBType getType() {
		return type;
	}

	public int getMaxLength() {
		return type.getMaxLength();
	}

	public String serialize(T obj) {
		return serializer.serialize(obj);
	}

	public T deserialize(String str) {
		return deserializer.deserialize(str);
	}

}
