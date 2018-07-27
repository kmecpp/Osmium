package com.kmecpp.osmium.api.config;

public interface Serializer<T> {
	
	String serialize(T obj);

}
