package com.kmecpp.osmium.api.config;

@FunctionalInterface
public interface ConfigSerializer {

	String serialize(Object o);

}
