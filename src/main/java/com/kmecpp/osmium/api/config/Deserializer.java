package com.kmecpp.osmium.api.config;

public interface Deserializer<T> {

	T deserialize(String str);

}
