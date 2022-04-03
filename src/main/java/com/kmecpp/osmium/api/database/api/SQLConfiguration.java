package com.kmecpp.osmium.api.database.api;

public class SQLConfiguration implements Cloneable {

	private final String host;
	private final String database;
	private final String username;
	private final String password;
	private final int port;
	private final String tablePrefix;

	private int minimumIdle = 2;
	private int maximumPoolSize = 10;
	private boolean allowMultiQueries;

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

	public int getMinimumIdle() {
		return minimumIdle;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public boolean isAllowMultiQueries() {
		return allowMultiQueries;
	}

	public SQLConfiguration withPoolSize(int minimumIdle, int maximumPoolSize) {
		this.minimumIdle = minimumIdle;
		this.maximumPoolSize = maximumPoolSize;
		return this;
	}

	public SQLConfiguration withAllowMultiQueries(boolean allowMultiQueries) {
		this.allowMultiQueries = allowMultiQueries;
		return this;
	}

	@Override
	public SQLConfiguration clone() {
		try {
			return (SQLConfiguration) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
