package com.kmecpp.osmium.api.database.api;

public class SQLConfig implements Cloneable {

	private final String tablePrefix;
	private final String host;
	private final String database;
	private final String username;
	private final String password;
	private final int port;

	private int minimumIdle = 2;
	private int maximumPoolSize = 10;
	private boolean allowMultiQueries;

	private SQLConfig(String tablePrefix, String host, int port, String database, String username, String password) {
		this.tablePrefix = tablePrefix;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public static SQLConfig of(String host, int port, String database, String username, String password) {
		return new SQLConfig("", host, port, database, username, password);
	}

	public static SQLConfig of(String tablePrefix, String host, int port, String database, String username, String password) {
		return new SQLConfig(tablePrefix, host, port, database, username, password);
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

	public int getMinimumIdle() {
		return minimumIdle;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public boolean isAllowMultiQueries() {
		return allowMultiQueries;
	}

	public SQLConfig withPoolSize(int minimumIdle, int maximumPoolSize) {
		this.minimumIdle = minimumIdle;
		this.maximumPoolSize = maximumPoolSize;
		return this;
	}

	public SQLConfig withAllowMultiQueries(boolean allowMultiQueries) {
		this.allowMultiQueries = allowMultiQueries;
		return this;
	}

	@Override
	public SQLConfig clone() {
		try {
			return (SQLConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
