package com.kmecpp.osmium.api.persistence;

public interface Deserializer<T> {

	T deserialize(String str);

}
