package com.kmecpp.osmium.api.database.sqlite;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import com.kmecpp.osmium.api.database.OrderBy;
import com.kmecpp.osmium.api.database.ResultSetProcessor;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.sqlite.DatabaseQueue.QueueExecutor;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.IOUtil;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.core.CoreOsmiumConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

public class Database extends SQLDatabase {

	private OsmiumPlugin plugin;
	private boolean usingMySql;

	private DatabaseQueue queue;
	private CountDownLatch latch = new CountDownLatch(1);

	private final HashMap<Class<?>, TableProperties> tables = new HashMap<>();

	public Database(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	public void start() {
		HikariConfig config = new HikariConfig();

		try {
			//			if (CoreOsmiumConfig.Database.useMySql) {
			//				OsmiumLogger.info("Using MySQL for database storage");
			//				usingMySql = true;
			//
			//				config.setJdbcUrl("jdbc:mysql://" + CoreOsmiumConfig.Database.host + ":" + CoreOsmiumConfig.Database.port + "/" + CoreOsmiumConfig.Database.database);
			//				config.setDriverClassName("com.mysql.jdbc.Driver");
			//				config.setUsername(CoreOsmiumConfig.Database.username);
			//				config.setPassword(CoreOsmiumConfig.Database.password);
			//				config.setConnectionTestQuery("USE " + CoreOsmiumConfig.Database.database);
			//
			//				source = new HikariDataSource(config);
			//			} else {
			OsmiumLogger.info("Using SQLite for database storage");
			config.setJdbcUrl("jdbc:sqlite:" + plugin.getFolder() + File.separator + "data.db");
			config.setDriverClassName("org.sqlite.JDBC");
			config.setConnectionTestQuery("SELECT 1");
			//			}
			config.setMinimumIdle(2);
			config.setMaximumPoolSize(10);
			config.setConnectionTimeout(3000L);
			source = new HikariDataSource(config);
			latch.countDown();
		} catch (PoolInitializationException e) {
			OsmiumLogger.error("Invalid database configuration! Failed to execute: '" + config.getConnectionTestQuery() + "'");
			e.printStackTrace();
		}
		queue = new DatabaseQueue(this);
		queue.start();
	}

	public final Collection<TableProperties> getTables() {
		return tables.values();
	}

	public final TableProperties getTable(Class<?> tableClass) {
		return tables.get(tableClass);
	}

	public final String getTableName(Class<?> tableClass) {
		return tables.get(tableClass).getName();
	}

	public void replaceInto(Class<?> cls, Object obj) {
		update(DBUtil.createReplaceInto(this, cls, obj));
	}

	public void replaceIntoAsync(Class<?> cls, Object obj) {
		updateAsync(DBUtil.createReplaceInto(this, cls, obj));
	}

	/**
	 * Queues the SQL query to execute asynchronously sometime in the future.
	 * This method has the same effect as executeUpdate() but does not wait for
	 * a response, resulting in an execution time ~5µs on average, as well as
	 * having the benefit of not having the server hang on a slow connection
	 * 
	 * @param update
	 *            the SQL query to execute
	 */
	public void updateAsync(String update) {
		queue.queue(update);
	}

	/**
	 * Executes the given SQL statement which may be an INSERT, UPDATE, or
	 * DELETE statement or an SQL statement that returns nothing, such as an SQL
	 * DDL statement.
	 * 
	 * @param update
	 *            the SQL statement to execute
	 */
	public int update(String update) {
		OsmiumLogger.debug("Executing update" + (Thread.currentThread().getClass().equals(QueueExecutor.class) ? " asynchronously" : "") + ": \"" + update + "\"");
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			return statement.executeUpdate(update);
		} catch (SQLException e) {
			OsmiumLogger.error("Failed to execute database update!");
			if (!CoreOsmiumConfig.debug) {
				OsmiumLogger.error("Failed update: '" + update + "'");
			}
			e.printStackTrace();
			return -1;
		} finally {
			IOUtil.close(connection, statement);
		}
	}

	public void setAll(Class<?> tableClass, String column, Object value) {
		update("UPDATE " + getTableName(tableClass) + " SET " + DBUtil.getColumnName(column) + "='" + value + "'");
	}

	public <T> Optional<T> getFirst(Class<T> tableClass, OrderBy orderBy, String columns, Object... values) {
		TableProperties properties = tables.get(tableClass);
		ArrayList<T> result = this.<T> query("SELECT * FROM " + properties.getName()
				+ " WHERE " + DBUtil.createWhere(columns.split(","), values)
				+ " " + orderBy + " LIMIT 1", properties);
		return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));

		//		TableProperties properties = tables.get(tableClass);
		//		DBResult result = query(tableClass, "SELECT * FROM " + properties.getName() + " WHERE " + DBUtil.createWhere(columns.split(","), values) + " " + orderBy);
		//		return result.isEmpty() ? null : result.first().as(tableClass);
		//		return orderBy(tableClass, orderBy, 1).get(0);
	}

	public int count(Class<?> tableClass) {
		TableProperties properties = tables.get(tableClass);
		return rawQuery("SELECT COUNT(*) FROM " + properties.getName(), r -> {
			try {
				return r.getInt(1);
			} catch (SQLException e) {
				return -1;
			}
		});
	}

	public <T> T getFirst(Class<T> tableClass, OrderBy orderBy) {
		return orderBy(tableClass, orderBy, 1).get(0);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int limit) {
		TableProperties properties = tables.get(tableClass);
		return query("SELECT * FROM " + properties.getName() + " " + orderBy + " LIMIT " + limit, properties);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, String orderBy, int min, int max) {
		return orderBy(tableClass, OrderBy.desc(orderBy), min, max);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int min, int max) {
		TableProperties properties = tables.get(tableClass);
		return query("SELECT * FROM " + properties.getName() + " " + orderBy + " LIMIT " + min + "," + max, properties);
	}

	public <T> T get(Class<T> tableClass, Object... primaryKeys) {
		return getOrDefault(tableClass, null, primaryKeys);
	}

	public <T> T getOrDefault(Class<T> tableClass, T defaultValue, Object... primaryKeys) {
		ArrayList<T> list = query(tableClass, primaryKeys);
		return list.isEmpty() ? defaultValue : list.get(0);
	}

	public <T> ArrayList<T> query(Class<T> tableClass, Object... primaryKeys) {
		return query(tableClass, (String[]) null, primaryKeys);
	}

	public <T> ArrayList<T> query(Class<T> tableClass, String columns, Object... values) {
		return query(tableClass, columns.split(","), values);
	}

	public <T> ArrayList<T> query(Class<T> tableClass, String[] columns, Object... values) {
		TableProperties table = tables.get(tableClass);
		if (columns == null) {
			columns = table.getPrimaryColumns();
		}

		String query = "SELECT * FROM " + table.getName() + " WHERE " + DBUtil.createWhere(columns, values);
		return query(query, table);
	}

	//	private <T> T newInstance(Class<T> cls, ResultSet rs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SQLException {
	//		TableProperties properties = getTable(cls);
	//
	//		T instance = cls.newInstance();
	//		for (Field field : properties.getFields()) {
	//			if (int.class.isAssignableFrom(cls) || Integer.class.isAssignableFrom(cls)) {
	//				field.set(instance, rs.getInt(field.getName()));
	//			} else if (long.class.isAssignableFrom(cls) || Long.class.isAssignableFrom(cls)) {
	//				field.set(instance, rs.getLong(field.getName()));
	//			} else if (float.class.isAssignableFrom(cls) || Float.class.isAssignableFrom(cls)) {
	//				field.set(instance, rs.getFloat(field.getName()));
	//			} else if (double.class.isAssignableFrom(cls) || Double.class.isAssignableFrom(cls)) {
	//				field.set(instance, rs.getDouble(field.getName()));
	//			} else if (boolean.class.isAssignableFrom(cls) || Boolean.class.isAssignableFrom(cls)) {
	//				field.set(instance, rs.getBoolean(field.getName()));
	//			} else if (String.class.isAssignableFrom(cls)) {
	//				field.set(instance, rs.getString(field.getName()));
	//			} else {
	//				//				types.get
	//				//				field.set(instance, rs.getInt(field.getName()));
	//			}
	//		}
	//
	//		T obj = cls.newInstance();
	//		return obj;
	//	}

	public <T> ArrayList<T> query(String query, Class<?> tableClass) {
		return query(query, tables.get(tableClass));
	}

	/**
	 * Executes an SQL query on the database and gets the result
	 * 
	 * @param query
	 *            the query to execute
	 * @return the result of the query
	 */
	public <T> ArrayList<T> query(String query, TableProperties properties) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Field[] fields = properties.getFields();
			ArrayList<T> result = new ArrayList<>();

			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				T obj = Reflection.createInstance(properties.<T> getTableClass());

				for (int i = 0; i < fields.length; i++) {
					fields[i].set(obj, Serialization.deserialize(properties.getFields()[i].getType(), (resultSet.getString(i + 1))));
				}
				result.add(obj);
			}
			return result;
		} catch (Exception e) {
			OsmiumLogger.error("Failed to execute database query: \"" + query + "\"");
			e.printStackTrace();
			return null;
		} finally {
			IOUtil.close(connection, statement, resultSet);
		}
	}

	public <T> T rawQuery(String query, ResultSetProcessor<T> processor) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			return processor.process(resultSet);
		} catch (Exception e) {
			throw new RuntimeException("Failed to execute database query: \"" + query + "\"");
		} finally {
			IOUtil.close(connection, statement, resultSet);
		}
	}

	/**
	 * Executes an SQL query on the database and gets the result
	 * 
	 * @param query
	 *            the query to execute
	 * @return the result of the query
	 */
	public <T> void query(String query, ResultSetProcessor<T> processor) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			processor.process(resultSet);
		} catch (SQLException e) {
			OsmiumLogger.error("Failed to execute database query: \"" + query + "\"");
			e.printStackTrace();
		} finally {
			IOUtil.close(connection, statement, resultSet);
		}
	}

	//	/**
	//	 * Executes an SQL query on the database that should return a single row
	//	 * 
	//	 * @param query
	//	 *            the query to execute
	//	 * @return the row retrieved
	//	 */
	//	public DBRow queryRow(String query) {
	//		return query(query).only();
	//	}
	//
	//	/**
	//	 * Filters the rows of the specified table with the given filters. Filters
	//	 * should be valid SQLite WHERE clauses.
	//	 * 
	//	 * @param table
	//	 *            the table name to query
	//	 * @param filters
	//	 *            the filters to apply
	//	 * @return the results matching the given filters
	//	 */
	//	public DBResult filter(String table, String... filters) {
	//		return query("SELECT * FROM " + table + " WHERE " + StringUtil.join(filters, " AND "));
	//	}

	/**
	 * Gets whether or not the server is using MySQL for database storage or not
	 * 
	 * @return true if MySQL is being used and false if not
	 */
	public boolean usingMySql() {
		return usingMySql;
	}

	public void renameTable(String oldName, String newName) {
		update("ALTER TABLE " + oldName + " RENAME TO " + newName);
	}

	/**
	 * Creates a database table with the given name representing the specified
	 * class. If the table already exists, this method will fail silently.
	 * 
	 * @param cls
	 *            the class containing the table data
	 */
	public void createTable(Class<?> cls) {
		if (tables.containsKey(cls)) {
			throw new IllegalStateException("Database table '" + cls.getName() + "' already exists!");
		}

		if (source == null) {
			start();
		}

		TableProperties data = new TableProperties(this, cls);
		tables.put(cls, data);

		try {
			Class.forName(cls.getName()); //Call static initializer
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		//		DBResult result = query("PRAGMA TABLE_INFO(" + data.getName() + ")");
		//
		//		if (result != null) {
		//			//Fix structure
		//			if (result.size() != data.getColumnCount()) {
		//				OsmiumLogger.warning("Database table structure modification detected! Attempting to repair changes...");
		//				String tempName = "'old_" + name + "." + System.currentTimeMillis() + "'";
		//				update("ALTER TABLE " + name + " RENAME TO " + tempName + ";"
		//						+ DBUtil.createTable(name, data)
		//						+ "INSERT INTO " + name + " SELECT " + (result.size() > data.getColumnCount()
		//								? StringUtil.join(data.getColumns(), ", ") //DELETED
		//								: "*") //ADDED
		//						+ " FROM " + tempName + ";");
		//			}
		//
		//			//		//Fix types
		//			//		for (Field field : data.getFields()) {
		//			//		}
		//		}

		update(DBUtil.createTable(this, data));
	}

	/**
	 * Gets a connection from the connection pool or null if a Connection cannot
	 * be established
	 * 
	 * @return a Connection to the data source or null
	 */
	public Connection getConnection() {
		if (source == null) {
			throw new IllegalStateException("Database has not been initialized!");
		}
		try {
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return source.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isActive() {
		return !source.isClosed();
	}

	public boolean isClosed() {
		return source.isClosed();
	}

	/**
	 * Shuts down the connection pool
	 */
	public void shutdown() {
		if (!source.isClosed()) {
			queue.flush(); //Queue should already have connection
			source.close();
		}
	}

	public void reload() {
		OsmiumLogger.info("Reestablishing database connection");
		latch = new CountDownLatch(1);
		shutdown();
		start();
	}

}
