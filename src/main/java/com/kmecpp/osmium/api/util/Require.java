package com.kmecpp.osmium.api.util;

import java.util.Collection;

public class Require {

	public static <T> T nonNull(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException("Require non null value");
		}
		return obj;
	}

	public static <T> T nonNull(T obj, T defaultValue) {
		if (obj == null) {
			return defaultValue;
		}
		return obj;
	}

	public static int lessThan(int n, int test) {
		if (n >= test) {
			throw new IllegalArgumentException("Require value less than " + test);
		}
		return n;
	}

	public static int nonNegative(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Require non negative value");
		}
		return n;
	}

	public static <T extends Collection<?>> T nonEmpty(T collection) {
		if (collection == null || collection.isEmpty()) {
			throw new IllegalArgumentException("Require non empty value");
		}
		return collection;
	}

}
