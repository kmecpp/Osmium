package com.kmecpp.osmium.api.database;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import com.kmecpp.osmium.api.database.DatabaseQueue.QueueExecutor;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.IOUtil;
import com.kmecpp.osmium.core.CoreOsmiumConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

public class Database {

	private OsmiumPlugin plugin;
	private boolean usingMySql;

	private DatabaseQueue queue;
	private HikariDataSource source;
	private CountDownLatch latch = new CountDownLatch(1);

	private final HashMap<Class<?>, TableProperties> tables = new HashMap<>();
	//	private final HashMap<String, Class<? extends CustomSerialization>> types = new HashMap<>();
	//	private final HashMap<Class<? extends CustomSerialization>, String> typeIds = new HashMap<>();

	//	private static final HashMap<Class<?>, DBSerializationData<?>> types = new HashMap<>();
	//
	//	static {
	//		registerType(DBType.INTEGER, int.class, Integer::parseInt);
	//		registerType(DBType.INTEGER, Integer.class, Integer::parseInt);
	//		registerType(DBType.LONG, long.class, Long::parseLong);
	//		registerType(DBType.LONG, Long.class, Long::parseLong);
	//		registerType(DBType.FLOAT, float.class, Float::parseFloat);
	//		registerType(DBType.FLOAT, Float.class, Float::parseFloat);
	//		registerType(DBType.DOUBLE, double.class, Double::parseDouble);
	//		registerType(DBType.DOUBLE, Double.class, Double::parseDouble);
	//		registerType(DBType.BOOLEAN, boolean.class, Boolean::parseBoolean);
	//		registerType(DBType.BOOLEAN, Boolean.class, Boolean::parseBoolean);
	//		registerType(DBType.STRING, String.class, (s) -> s);
	//		registerType(DBType.SERIALIZABLE, UUID.class, UUID::fromString);
	//		registerType(DBType.SERIALIZABLE, Location.class, Location::fromString);
	//		registerType(DBType.SERIALIZABLE, SimpleDate.class, SimpleDate::fromString);
	//		registerType(DBType.SERIALIZABLE, Inventory.class, JavaSerializer::deserialize);
	//	}
	//
	//	public static final <T> void registerType(DBType type, Class<T> cls, Deserializer<T> deserializer) {
	//		types.put(cls, new DBSerializationData<>(type, cls, String::valueOf, deserializer));
	//	}

	//	static {
	//		registerType("Day", SimpleDate.class);
	//		registerType("List", DBList.class);
	//	}

	public Database(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	public void start() {
		//TODO: Implement MySQL
		//		if (CoreOsmiumConfig.Database.enableMysql) {
		//			OsmiumLogger.error("MySQL is is not fully supported yet! Switching to SQLite");
		//			CoreOsmiumConfig.Database.enableMysql = false;
		//		}

		HikariConfig config = new HikariConfig();

		try {
			if (CoreOsmiumConfig.Database.useMySql) {
				OsmiumLogger.info("Using MySQL for database storage");
				usingMySql = true;

				config.setJdbcUrl("jdbc:mysql://" + CoreOsmiumConfig.Database.host + ":" + CoreOsmiumConfig.Database.port + "/" + CoreOsmiumConfig.Database.database);
				config.setDriverClassName("com.mysql.jdbc.Driver");
				config.setUsername(CoreOsmiumConfig.Database.username);
				config.setPassword(CoreOsmiumConfig.Database.password);
				config.setConnectionTestQuery("USE " + CoreOsmiumConfig.Database.database);

				source = new HikariDataSource(config);
			} else {
				OsmiumLogger.info("Using SQLite for database storage");
				config.setJdbcUrl("jdbc:sqlite:" + plugin.getFolder() + File.separator + "data.db");
				config.setDriverClassName("org.sqlite.JDBC");
				config.setConnectionTestQuery("SELECT 1");
			}
			config.setMinimumIdle(3);
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

	//	public void registerType(String id, Class<? extends CustomSerialization> cls) {
	//		types.put(id, cls);
	//		typeIds.put(cls, id);
	//	}

	//	public String getTypeId(Class<?> cls) {
	//		return typeIds.get(cls);
	//	}

	//	@SuppressWarnings("unchecked")
	//	public <T> DBSerializationData<T> getSerializationData(Class<T> cls) {
	//		return (DBSerializationData<T>) types.get(cls);
	//	}

	//	public String serialize(Object obj) {
	//		if (obj == null) {
	//			return "null";
	//		}
	//		@SuppressWarnings("unchecked")
	//		DBSerializationData<Object> d = (DBSerializationData<Object>) types.get(obj.getClass());
	//		if (d != null) {
	//			return d.serialize(obj);
	//		}
	//		throw new RuntimeException("Class has no registered serializer: " + obj.getClass());
	//
	//		//		if (typeIds.containsKey(obj.getClass())) {
	//		//			if (obj instanceof CustomSerialization) {
	//		//				return ((CustomSerialization) obj).serialize();
	//		//			} else if (obj instanceof java.io.Serializable) {
	//		//				return JavaSerializer.serialize((java.io.Serializable) obj);
	//		//			}
	//		//		}
	//
	//	}

	//	public Object deserialize(String key, String value) {
	//		try {
	//			Class<?> cls = types.get(key);
	//			if (CustomSerialization.class.isAssignableFrom(cls)) {
	//				Constructor<?> constructor = cls.getConstructor(String.class);
	//				constructor.setAccessible(true);
	//				return constructor.newInstance(value);
	//			} else {
	//				return JavaSerializer.deserialize(value);
	//			}
	//		} catch (Exception e) {
	//			throw new RuntimeException("Failed to deserialize string with key: '" + key + "', string: '" + value + "'");
	//		}
	//
	//	}

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

	public static final boolean isSerializable(Class<?> cls) {
		//		return typeKeys.containsKey(cls);
		//		return cls ins
		return false;
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
	public void update(String update) {
		OsmiumLogger.debug("Executing update" + (Thread.currentThread().getClass().equals(QueueExecutor.class) ? " asynchronously" : "") + ": \"" + update + "\"");
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(update);
		} catch (SQLException e) {
			OsmiumLogger.error("Failed to execute database update!");
			if (!CoreOsmiumConfig.debug) {
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
		TableProperties properties = tables.get(tableClass);
		return query(tableClass, "SELECT * FROM " + properties.getName()
				+ " WHERE " + DBUtil.createWhere(columns.split(","), values)
				+ " " + orderBy + " LIMIT 1").get(0);

		//		TableProperties properties = tables.get(tableClass);
		//		DBResult result = query(tableClass, "SELECT * FROM " + properties.getName() + " WHERE " + DBUtil.createWhere(columns.split(","), values) + " " + orderBy);
		//		return result.isEmpty() ? null : result.first().as(tableClass);
		//		return orderBy(tableClass, orderBy, 1).get(0);
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

	private <T> ArrayList<T> query(Class<T> tableClass, String[] columns, Object... values) {
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

			ArrayList<T> list = new ArrayList<>();

			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				T obj = properties.<T> getTableClass().newInstance();

				for (int i = 0; i < fields.length; i++) {
					//					Object value = ;
					//					value = ;
					//					if (value instanceof String) {
					//						DBSerializationData<?> sd = types.get(properties.getFields()[i].getType());
					//						if (sd != null) {
					//							value = sd.deserialize((String) value);
					//						} else {
					//							IOUtil.close(connection, statement, resultSet); //TODO: Why is Eclipse complaining if this is not here? 
					//							throw new IllegalArgumentException("");
					//						}
					//						//						types
					//					}
					//					DBSerializationData<?> sd = types.get(properties.getFields()[i].getType());
					fields[i].set(obj, Serialization.deserialize(properties.getFields()[i].getType(), (resultSet.getString(i + 1))));
				}
				list.add(obj);
			}
			return list;
		} catch (Exception e) {
			OsmiumLogger.error("Failed to execute database query: \"" + query + "\"");
			e.printStackTrace();
			return null;
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
	public void query(String query, ResultSetProcessor processor) {
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
