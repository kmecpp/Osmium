package com.kmecpp.osmium.api.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.Completer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

public class MySQLDatabase {

	private static final ExecutorService scheduler = Executors.newFixedThreadPool(2);

	private HikariDataSource source;

	public MySQLDatabase(String host, int port, String database, String username, String password) {
		HikariConfig config = new HikariConfig();

		try {
			OsmiumLogger.info("Using MySQL for database storage");

			config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
			config.setDriverClassName("com.mysql.jdbc.Driver");
			config.setUsername(username);
			config.setPassword(password);
			config.setConnectionTestQuery("USE " + database);

			source = new HikariDataSource(config);

			config.setMinimumIdle(2);
			config.setMaximumPoolSize(10);
			config.setConnectionTimeout(1000L);
			source = new HikariDataSource(config);
			OsmiumLogger.info("Successfully established connection to MySQL database: " + database);
		} catch (PoolInitializationException ex) {
			OsmiumLogger.error("Invalid database configuration! Failed to execute: '" + config.getConnectionTestQuery() + "'");
			ex.printStackTrace();
		}
	}

	public static void reload() {

	}

	public boolean isConnected() {
		return source != null && !source.isClosed();//&& source.isRunning();
	}

	public void shutdown() {
		source.close();
	}

	public int preparedStatement(String update, PreparedStatementBuilder builder) {
		return preparedStatement(update, builder, null);
	}

	public <T> T query(String query, Function<ResultSet, T> handler) {
		if (source != null) {
			Statement statement = null;
			ResultSet resultSet = null;
			try (Connection connection = source.getConnection()) {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);
				return handler.apply(resultSet);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(statement, resultSet);
			}
		}
		return null;
	}

	public void query(String query, Consumer<ResultSet> handler) {
		query(query, rs -> {
			handler.accept(rs);
			return null;
		});
	}

	public int update(String update) {
		System.out.println("Executing update: " + update);
		if (source != null) {
			Statement statement = null;
			ResultSet resultSet = null;
			try (Connection connection = source.getConnection()) {
				statement = connection.createStatement();
				return statement.executeUpdate(update);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(statement, resultSet);
			}
		}
		return -1;
	}

	public Completer updateAsync(String update) {
		Completer completer = new Completer();
		scheduler.submit(() -> {
			update(update);
			completer.complete();
		});
		return completer;
	}

	public void updateAsync(String update, PreparedStatementBuilder builder) {
		scheduler.submit(() -> preparedStatement(update, builder));
	}

	public int preparedStatement(String update, PreparedStatementBuilder builder, Consumer<ResultSet> handler) {
		//		System.out.println("Executing statement: " + update);
		if (source != null) {
			PreparedStatement statement = null;
			ResultSet resultSet = null;
			try (Connection connection = source.getConnection()) {
				statement = connection.prepareStatement(update);
				builder.build(statement);
				boolean result = statement.execute();
				if (handler != null) {
					handler.accept(statement.getResultSet());
				}
				return result ? 1 : -1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(statement, resultSet);
			}
		}
		return -1;
	}

	private void close(AutoCloseable... close) {
		for (AutoCloseable closeable : close) {
			if (closeable == null) {
				continue;
			}
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
