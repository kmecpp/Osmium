package com.kmecpp.osmium.api.persistence;

public interface Serializer<T> {

	public static final Serializer<?> DEFAULT = String::valueOf;

	String serialize(T obj);

	@SuppressWarnings("unchecked")
	public static <T> T getDefault() {
		return (T) DEFAULT;
	}

}
