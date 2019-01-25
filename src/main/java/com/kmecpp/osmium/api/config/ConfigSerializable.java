package com.kmecpp.osmium.api.config;

public interface ConfigSerializable {

	void write(TypeData data);

	void read(TypeData data);

}
