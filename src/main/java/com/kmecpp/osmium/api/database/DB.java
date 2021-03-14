package com.kmecpp.osmium.api.database;

public class DB {

	public static Filter where(String filter, Object value) {
		return Filter.of(filter, value);
	}

}
