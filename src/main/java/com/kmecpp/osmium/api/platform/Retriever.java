package com.kmecpp.osmium.api.platform;

@FunctionalInterface
public interface Retriever<T> {

	T get();

}
