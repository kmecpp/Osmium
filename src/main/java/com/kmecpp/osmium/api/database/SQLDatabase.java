package com.kmecpp.osmium.api.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.kmecpp.osmium.api.database.mysql.PreparedStatementBuilder;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.StringUtil;
import com.zaxxer.hikari.HikariDataSource;

public abstract class SQLDatabase {

	private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);

	protected HikariDataSource source;

	public <T> T get(String query, ResultSetProcessor<T> handler) {
		return getOrDefault(query, null, rs -> {
			rs.next();
			return handler.process(rs);
		});
	}

	public <T> T getOrDefault(String query, Object defaultValue, ResultSetProcessor<T> handler) {
		if (source != null) {
			Statement statement = null;
			ResultSet resultSet = null;
			try (Connection connection = source.getConnection()) {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);
				if (!resultSet.isBeforeFirst()) {
					return null;
				}
				return handler.process(resultSet);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				close(statement, resultSet);
			}
		}
		return null;
	}

	public <T> T get(Class<T> tableClass, Object... primaryKeys) {
		return getOrDefault(tableClass, null, primaryKeys);
	}

	public <T> T get(Class<T> tableClass, String columns, Object... primaryKeys) {
		return getOrDefault(tableClass, null, columns, primaryKeys);
	}

	public <T> T getOrDefault(Class<T> tableClass, T defaultValue, Object... primaryKeys) {
		return getOrDefault(tableClass, defaultValue, (String[]) null, primaryKeys);
	}

	public <T> T getOrDefault(Class<T> tableClass, T defaultValue, String columns, Object... primaryKeys) {
		return getOrDefault(tableClass, defaultValue, columns.split(","), primaryKeys);
	}

	public <T> T getOrDefault(Class<T> tableClass, T defaultValue, String[] columns, Object... primaryKeys) {
		ArrayList<T> list = query(tableClass, columns, primaryKeys);
		if (list.isEmpty()) {
			return defaultValue;
		} else if (list.size() != 1) {
			throw new IllegalStateException("Database query returned multiple rows: " + list.size());
		} else {
			return list.get(0);
		}
	}

	public <T> ArrayList<T> query(Class<T> tableClass, Object... values) {
		return query(tableClass, (String[]) null, values);
	}

	public <T> ArrayList<T> query(Class<T> tableClass, String columns, Object... values) {
		return queryColumns(tableClass, columns, values);
	}

	public <T> ArrayList<T> queryColumns(Class<T> tableClass, String columns, Object... values) {
		return query(tableClass, columns.split(","), values);
	}

	public abstract <T> ArrayList<T> query(Class<T> tableClass, String[] columns, Object... values);

	public abstract void replaceInto(Class<?> tableClass, Object obj);

	public void replaceIntoAsync(Class<?> tableClass, Object obj) {
		updateAsync(() -> replaceInto(tableClass, obj));
	}

	public int update(String update) {
		if (source == null) {
			OsmiumLogger.error("Failed to run update on closed database: " + update);
			for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
				OsmiumLogger.error("    " + e);
			}
			return -1;
		}
		OsmiumLogger.debug("Executing raw update: " + update);
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
		return -1;
	}

	public void updateAsync(Runnable runnable) {
		threadPool.submit(runnable);
	}

	public int preparedStatement(String update, PreparedStatementBuilder builder) {
		return preparedStatement(update, builder, null);
	}

	public int preparedStatement(String update, PreparedStatementBuilder builder, Consumer<ResultSet> handler) {
		//		System.out.println("Executing statement: " + update);
		OsmiumLogger.debug("Executing prepared statement: " + update);
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

	//	public <T> T queryAsync(Callable<T> callable) {
	//		threadPool
	//	}

	protected void close(AutoCloseable... close) {
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

	public static String getColumnName(String fieldName) {
		return StringUtil.normalize(fieldName, "_");
	}

}
