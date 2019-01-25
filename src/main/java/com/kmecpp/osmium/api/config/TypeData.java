package com.kmecpp.osmium.api.config;

import java.util.HashMap;
import java.util.UUID;

public class TypeData {

	private HashMap<String, String> data = new HashMap<>();

	public HashMap<String, String> getData() {
		return data;
	}

	public void write(String key, String value) {
		data.put(key, value);
	}

	public void write(String key, int value) {
		data.put(key, String.valueOf(value));
	}

	public void write(String key, double value) {
		data.put(key, String.valueOf(value));
	}

	public void write(String key, boolean value) {
		data.put(key, String.valueOf(value));
	}

	public void write(String key, UUID value) {
		data.put(key, String.valueOf(value));
	}

	public String get(String key) {
		return data.get(key);
	}

	public int getInt(String key) {
		return Integer.parseInt(data.get(key));
	}

	public double getDouble(String key) {
		return Double.parseDouble(data.get(key));
	}

	public UUID getId(String key) {
		return UUID.fromString(data.get(key));
	}

	public boolean getBoolean(String key) {
		if (key.equalsIgnoreCase("true")) {
			return true;
		} else if (key.equalsIgnoreCase("false")) {
			return false;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
