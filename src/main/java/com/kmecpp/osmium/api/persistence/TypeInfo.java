package com.kmecpp.osmium.api.persistence;

public class TypeInfo<T> {

	private Class<?> cls;
	private Serializer<T> serializer;
	private Deserializer<T> deserializer;

	public TypeInfo(Class<?> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
		this.cls = cls;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	public Class<?> getTypeClass() {
		return cls;
	}

	public Serializer<T> getSerializer() {
		return serializer;
	}

	public Deserializer<T> getDeserializer() {
		return deserializer;
	}

}
