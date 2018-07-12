package com.kmecpp.osmium.api.database;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.bukkit.Bukkit;

import com.kmecpp.jlib.utils.IOUtil;
import com.kmecpp.jlib.utils.StringUtil;
import com.kmecpp.osmium.OsmiumConfiguration;
import com.kmecpp.osmium.OsmiumLogger;
import com.kmecpp.osmium.api.database.DatabaseQueue.QueueExecutor;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

public class Database {

	private OsmiumPlugin plugin;
	private boolean usingMySql;

	private DatabaseQueue queue;
	private HikariDataSource source;
	private CountDownLatch latch = new CountDownLatch(1);

	private static final HashMap<Class<?>, TableProperties> tables = new HashMap<>();
	private static final HashMap<String, Class<? extends CustomSerialization>> types = new HashMap<>();
	private static final HashMap<Class<? extends CustomSerialization>, String> typeIds = new HashMap<>();

	//	static {
	//		registerType("Day", SimpleDate.class);
	//		registerType("List", DBList.class);
	//	}

	public Database(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	public void start() {
		try {
			HikariConfig config = new HikariConfig();
			if (OsmiumConfiguration.enableMySQL) {
				OsmiumLogger.info("Using MySQL for database storage");
				usingMySql = true;

				config.setJdbcUrl("jdbc:mysql://" + OsmiumConfiguration.mysqlHost + ":" + OsmiumConfiguration.mysqlPort + "/" + OsmiumConfiguration.mysqlDatabase);
				config.setDriverClassName("com.mysql.jdbc.Driver");
				config.setUsername(OsmiumConfiguration.mysqlUsername);
				config.setPassword(OsmiumConfiguration.mysqlPassword);

				source = new HikariDataSource(config);
			} else {
				OsmiumLogger.info("Using SQLite for database storage");
				config.setJdbcUrl("jdbc:sqlite:" + plugin.getPluginFolder().toFile().getPath() + File.separator + "data.db");
				config.setDriverClassName("org.sqlite.JDBC");
			}
			config.setMinimumIdle(3);
			config.setMaximumPoolSize(10);
			config.setConnectionTimeout(3000L);
			config.setConnectionTestQuery("SELECT 1");
			source = new HikariDataSource(config);
			latch.countDown();
		} catch (PoolInitializationException e) {
			OsmiumLogger.error("Invalid database configuration!");
			e.printStackTrace();
			Bukkit.getServer().shutdown();
		}
		queue = new DatabaseQueue(this);
		queue.start();
	}

	public static void registerType(String id, Class<? extends CustomSerialization> cls) {
		types.put(id, cls);
		typeIds.put(cls, id);
	}

	public static String getTypeId(Class<?> cls) {
		return typeIds.get(cls);
	}

	public static String serialize(Object obj) {
		if (typeIds.containsKey(obj.getClass())) {
			if (obj instanceof CustomSerialization) {
				return ((CustomSerialization) obj).serialize();
			} else if (obj instanceof java.io.Serializable) {
				return StringUtil.serialize((java.io.Serializable) obj);
			}
		}
		throw new RuntimeException("Object is not serializable: " + obj.getClass());

	}

	public static Object deserialize(String key, String value) {
		try {
			Class<?> cls = types.get(key);
			if (CustomSerialization.class.isAssignableFrom(cls)) {
				Constructor<?> constructor = cls.getConstructor(String.class);
				constructor.setAccessible(true);
				return constructor.newInstance(value);
			} else {
				return StringUtil.deserialize(value);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize string with key: '" + key + "', string: '" + value + "'");
		}

	}

	//	public static Class<?> getSerializable(String id) {
	//		return typesId.get(id).getJavaClass();
	//	}
	//
	//	public static String serialize(Object obj) {
	//		if (obj instanceof Inventory) {
	//			return Serialize.playerInventory((Inventory) obj);
	//		} else if (obj instanceof Location) {
	//			return Serialize.location((Location) obj);
	//		}
	//		return String.valueOf(obj);
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	public static <T> T deserialize(String id, String str) {
	//		CustomType<T> type = (CustomType<T>) typesId.get(id);
	//		if (type != null) {
	//			return type.deserialize(str);
	//		}
	//		throw new RuntimeException("No deserializer registered for ID: " + id);
	//	}

	public static boolean isSerializable(Class<?> cls) {
		//		return typeKeys.containsKey(cls);
		//		return cls ins
		return false;
	}

	public static Collection<TableProperties> getTables() {
		return tables.values();
	}

	public static final TableProperties getTable(Class<?> tableClass) {
		return tables.get(tableClass);
	}

	public static String getTableName(Class<?> tableClass) {
		return tables.get(tableClass).getName();
	}

	public void replaceInto(Class<?> cls, Object obj) {
		updateAsync(DBUtil.createReplaceInto(cls, obj));
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
	public void update(String update) {
		OsmiumLogger.debug("Executing update" + (Thread.currentThread().getClass().equals(QueueExecutor.class) ? " asynchronously" : "") + ": '" + update + "'");
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(update);
		} catch (SQLException e) {
			OsmiumLogger.error("Failed to execute database update!");
			if (!OsmiumConfiguration.debug) {
				OsmiumLogger.error("Failed update: '" + update + "'");
			}
			e.printStackTrace();
		} finally {
			IOUtil.close(connection, statement);
		}
	}

	public void setAll(Class<?> tableClass, String column, Object value) {
		update("UPDATE " + getTableName(tableClass) + " SET " + DBUtil.getColumnName(column) + "='" + value + "'");
	}

	public <T> T getFirst(Class<T> tableClass, OrderBy orderBy, String columns, Object... values) {
		DBResult result = query("SELECT * FROM " + tables.get(tableClass).getName() + " WHERE " + DBUtil.createWhere(columns.split(","), values) + " " + orderBy);
		return result.isEmpty() ? null : result.first().as(tableClass);
	}

	public <T> T getFirst(Class<T> tableClass, OrderBy orderBy) {
		return orderBy(tableClass, orderBy, 1).get(0);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int limit) {
		return query("SELECT * FROM " + tables.get(tableClass).getName() + " " + orderBy + " LIMIT " + limit).as(tableClass);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int min, int max) {
		return query("SELECT * FROM " + tables.get(tableClass).getName() + " " + orderBy + " LIMIT " + min + "," + max).as(tableClass);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, String orderBy, int min, int max) {
		return orderBy(tableClass, OrderBy.desc(orderBy), min, max);
	}

	public <T> T get(Class<T> tableClass, Object... primaryKeys) {
		return getOrDefault(tableClass, null, primaryKeys);
	}

	public <T> T getOrDefault(Class<T> tableClass, T def, Object... primaryKeys) {
		ArrayList<T> list = query(tableClass, primaryKeys);
		return list.isEmpty() ? def : list.get(0);
	}

	public <T> ArrayList<T> query(Class<T> tableClass, Object... primaryKeys) {
		return query(tableClass, (String[]) null, primaryKeys);
	}

	public <T> ArrayList<T> query(Class<T> tableClass, String columns, Object... values) {
		return query(tableClass, columns.split(","), values);
	}

	private <T> ArrayList<T> query(Class<T> tableClass, String[] columns, Object... values) {
		TableProperties table = tables.get(tableClass);
		if (columns == null) {
			columns = table.getPrimaryColumns();
		}

		ArrayList<T> list = new ArrayList<>();
		DBResult result = query("SELECT * FROM " + table.getName() + " WHERE " + DBUtil.createWhere(columns, values));
		for (int i = 0; i < result.size(); i++) {
			list.add(result.get(i).as(tableClass));
		}
		return list;
	}

	/**
	 * Executes an SQL query on the database and gets the result
	 * 
	 * @param query
	 *            the query to execute
	 * @return the result of the query
	 */
	public DBResult query(String query) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			OsmiumLogger.debug("Executing query: '" + query + "'");
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			return new DBResult(resultSet);
		} catch (SQLException e) {
			OsmiumLogger.error("Failed to execute database query: '" + query + "'");
			e.printStackTrace();
			return null;
		} finally {
			IOUtil.close(connection, statement, resultSet);
		}
	}

	/**
	 * Executes an SQL query on the database that should return a single row
	 * 
	 * @param query
	 *            the query to execute
	 * @return the row retrieved
	 */
	public DBRow queryRow(String query) {
		return query(query).only();
	}

	/**
	 * Filters the rows of the specified table with the given filters. Filters
	 * should be valid SQLite WHERE clauses.
	 * 
	 * @param table
	 *            the table name to query
	 * @param filters
	 *            the filters to apply
	 * @return the results matching the given filters
	 */
	public DBResult filter(String table, String... filters) {
		return query("SELECT * FROM " + table + " WHERE " + StringUtil.join(filters, " AND "));
	}

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
	 * @param name
	 *            the name of the table to create
	 * @param cls
	 *            the class whose database representation to create
	 */
	public void createTable(Class<?> cls) {
		TableProperties data = new TableProperties(cls);
		tables.put(cls, data);

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

		update(DBUtil.createTable(data));
	}

	/**
	 * Gets a connection from the connection pool or null if a Connection cannot
	 * be established
	 * 
	 * @return a Connection to the data source or null
	 */
	public Connection getConnection() {
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
