package com.kmecpp.osmium.api.database;

public class SQLConfiguration {

	private final String host;
	private final String database;
	private final String username;
	private final String password;
	private final int port;
	private final String tablePrefix;

	private int minIdle = 2;
	private int maxIdle = 10;

	public SQLConfiguration(String host, String database, String username, String password, int port, String tablePrefix) {
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;
		this.tablePrefix = tablePrefix;
	}

	public String getHost() {
		return host;
	}

	public String getDatabase() {
		return database;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

}
