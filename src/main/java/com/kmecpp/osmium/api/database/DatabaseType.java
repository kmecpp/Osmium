package com.kmecpp.osmium.api.database;

public enum DatabaseType {

	MYSQL("MySQL"),
	SQLITE("SQLite"),

	;

	private String name;

	private DatabaseType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
