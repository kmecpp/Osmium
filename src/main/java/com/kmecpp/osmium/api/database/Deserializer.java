package com.kmecpp.osmium.api.database;

public interface Deserializer<T> {

	T deserialize(String str);

}
