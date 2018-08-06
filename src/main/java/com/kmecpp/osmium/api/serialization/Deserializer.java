package com.kmecpp.osmium.api.serialization;

public interface Deserializer<T> {

	T deserialize(String str);

}
