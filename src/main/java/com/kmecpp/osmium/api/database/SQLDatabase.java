package com.kmecpp.osmium.api.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import com.kmecpp.osmium.api.database.mysql.MDBTableData;
import com.kmecpp.osmium.api.database.mysql.MDBUtil;
import com.kmecpp.osmium.api.database.mysql.PreparedStatementBuilder;
import com.kmecpp.osmium.api.database.sqlite.DBUtil;
import com.kmecpp.osmium.api.database.sqlite.DatabaseQueue;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Callback;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

public abstract class SQLDatabase {

	//	private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);
	protected final OsmiumPlugin plugin;
	protected final DatabaseType type;

	protected boolean initialized;
	protected DatabaseQueue queue;
	protected CountDownLatch availableLatch = new CountDownLatch(1);

	protected SQLConfiguration config;
	private HikariDataSource hikariSource;

	protected static final HashMap<Class<?>, MDBTableData> tables = new HashMap<>();

	public SQLDatabase(OsmiumPlugin plugin, DatabaseType type) {
		this.plugin = plugin;
		this.type = type;
	}

	public void configure(String host, int port, String database, String username, String password) {
		configure(null, host, port, database, username, password);
	}

	public void configure(String prefix, String host, int port, String database, String username, String password) {
		configure(new SQLConfiguration(host, database, username, password, port, prefix));
	}

	public void configure(SQLConfiguration config) {
		if (StringUtil.isNullOrEmpty(config.getDatabase())) {
			throw new IllegalArgumentException("Cannot initializing empty database for plugin: " + plugin.getName());
		}
		this.config = config;
	}

	public String getTablePrefix() {
		return config != null ? config.getTablePrefix() : "";
	}

	/**
	 * Generally no need to call this manually. The database is initialized
	 * automatically when tables are created.
	 */
	public void start() {
		HikariConfig hikariConfig = new HikariConfig();

		try {
			OsmiumLogger.info("Using " + type.getName() + " for database storage");
			if (type == DatabaseType.SQLITE) {
				hikariConfig.setJdbcUrl("jdbc:sqlite:" + plugin.getFolder() + File.separator + "data.db");
				hikariConfig.setDriverClassName("org.sqlite.JDBC");
				hikariConfig.setConnectionTestQuery("SELECT 1");
				OsmiumLogger.info("Successfully established SQLite connection!");
			} else {
				hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase());
				hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
				hikariConfig.setUsername(config.getUsername());
				hikariConfig.setPassword(config.getPassword());
				hikariConfig.setConnectionTestQuery("USE " + config.getDatabase());
				OsmiumLogger.info("Successfully established connection to " + type.getName() + " database: " + config.getDatabase());
			}

			hikariConfig.setMinimumIdle(2);
			hikariConfig.setMaximumPoolSize(10);
			hikariConfig.setConnectionTimeout(500L);
			hikariSource = new HikariDataSource(hikariConfig);
			availableLatch.countDown(); //Mark database as available
		} catch (PoolInitializationException e) {
			OsmiumLogger.error("Invalid database configuration! Failed to execute: '" + hikariConfig.getConnectionTestQuery() + "'");
			e.printStackTrace();
		}
		queue = new DatabaseQueue();
		queue.start();
	}

	public void query(String query, Consumer<ResultSet> handler) {
		get(query, rs -> {
			handler.accept(rs);
			return null;
		});
	}

	public <T> T get(String query, ResultSetProcessor<T> handler) {
		return getOrDefault(query, null, rs -> {
			rs.next();
			return handler.process(rs);
		});
	}

	public <T> T accumulate(String query, ResultSetProcessor<T> handler) {
		return getOrDefault(query, null, rs -> {
			return handler.process(rs);
		});
	}

	public <T> T getOrDefault(String query, Object defaultValue, ResultSetProcessor<T> handler) {
		OsmiumLogger.debug("Executing get query: " + query);
		Statement statement = null;
		ResultSet resultSet = null;
		try (Connection connection = getConnection()) {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (!resultSet.isBeforeFirst()) {
				return null;
			}
			return handler.process(resultSet);
		} catch (Exception e) {
			OsmiumLogger.warn("An error occurred while executing get query: " + query);
			e.printStackTrace();
		} finally {
			close(statement, resultSet);
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
		//			OsmiumLogger.error("Failed to run update on closed database: " + update);
		//			for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
		//				OsmiumLogger.error("    " + e);
		//			}
		//			return -1;
		OsmiumLogger.debug("Executing raw update: " + update);
		Statement statement = null;
		ResultSet resultSet = null;
		try (Connection connection = getConnection()) {
			statement = connection.createStatement();
			return statement.executeUpdate(update);
		} catch (Exception e) {
			OsmiumLogger.warn("An error occurred while executing update: " + update);
			e.printStackTrace();
		} finally {
			close(statement, resultSet);
		}
		return -1;
	}

	public void updateAsync(Runnable runnable) {
		//		threadPool.submit(runnable);
		queue.submit(runnable);
	}

	public Callback updateAsync(String update) {
		Callback completer = new Callback();
		queue.submit(() -> {
			int rowsUpdated = update(update);
			completer.complete(rowsUpdated);
		});
		return completer;
	}

	public Callback updateAsync(String update, PreparedStatementBuilder builder) {
		Callback completer = new Callback();
		queue.submit(() -> {
			int rowsUpdated = preparedUpdateStatement(update, builder);
			completer.complete(rowsUpdated);
		});
		return completer;
	}

	public int preparedUpdateStatement(String update, PreparedStatementBuilder builder) {
		return preparedUpdateStatement(update, builder, (Consumer<ResultSet>) null);
	}

	public int preparedUpdateStatement(String update, PreparedStatementBuilder builder, Consumer<ResultSet> handler) {
		//		System.out.println("Executing statement: " + update);
		OsmiumLogger.debug("Executing prepared statement: " + update);
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try (Connection connection = getConnection()) {
			statement = connection.prepareStatement(update);
			builder.build(statement);
			int result = statement.executeUpdate();
			if (handler != null) {
				handler.accept(statement.getResultSet());
			}
			return result;
		} catch (Exception e) {
			OsmiumLogger.warn("An error occurred while executing update: " + update);
			e.printStackTrace();
		} finally {
			close(statement, resultSet);
		}
		return -1;
	}

	public void preparedQueryStatement(String update, PreparedStatementBuilder builder) {
		preparedQueryStatement(update, builder, (Consumer<ResultSet>) null);
	}

	public void preparedQueryStatement(String update, PreparedStatementBuilder builder, Consumer<ResultSet> handler) {
		OsmiumLogger.debug("Executing prepared statement: " + update);

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try (Connection connection = getConnection()) {
			statement = connection.prepareStatement(update);
			builder.build(statement);
			resultSet = statement.executeQuery();
			if (handler != null) {
				handler.accept(resultSet);
			}
		} catch (Exception e) {
			OsmiumLogger.warn("An error occurred while executing query: " + update);
			e.printStackTrace();
		} finally {
			close(statement, resultSet);
		}
	}

	public <T> T query(String query, PreparedStatementBuilder builder, ResultSetProcessor<T> resultSetProcessor) {
		OsmiumLogger.warn("Executing prepared statement: " + query);

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try (Connection connection = getConnection()) {
			statement = connection.prepareStatement(query);
			builder.build(statement);
			statement.execute();
			return resultSetProcessor.process(statement.getResultSet());
		} catch (Exception e) {
			OsmiumLogger.error("An error occurred while executing query: '" + query + "'");
			e.printStackTrace();
		} finally {
			close(statement, resultSet);
		}
		return null;
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
		return StringUtil.normalizeCamelCase(fieldName, "_");
	}

	/**
	 * Gets a connection from the connection pool or null if a Connection cannot
	 * be established
	 * 
	 * @return a Connection to the data source or null
	 */
	public Connection getConnection() {
		if (hikariSource == null) {
			throw new IllegalStateException("Database has not been initialized!");
		}
		try {
			try {
				availableLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return hikariSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	//	public boolean isActive() {
	//		return !source.isClosed();
	//	}

	public boolean isConnected() {
		return hikariSource != null && !hikariSource.isClosed();//&& source.isRunning();
	}

	public boolean isClosed() {
		return hikariSource.isClosed();
	}

	/**
	 * Shuts down the connection pool
	 */
	public void shutdown() {
		if (hikariSource != null && !hikariSource.isClosed()) {
			queue.flush(); //Queue should already have connection
			hikariSource.close();
		}
	}

	public void reload() {
		OsmiumLogger.info("Reestablishing database connection");
		availableLatch = new CountDownLatch(1);
		shutdown();
		start();
	}

	public static MDBTableData getTable(Class<?> cls) {
		return tables.get(cls);
	}

	public MDBTableData getTableMeta(Class<?> cls) {
		MDBTableData data = tables.get(cls);
		if (data != null) {
			return data;
		}
		registerTable(cls);
		return tables.get(cls);
	}

	public void registerTable(Class<?> cls) {
		MDBTableData data = new MDBTableData(this, cls);
		tables.put(cls, data);
	}

	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Creates a database table with the given name representing the specified
	 * class. If the table already exists, this method will fail silently.
	 * 
	 * @param cls
	 *            the class containing the table data
	 */
	public void createTable(Class<?> cls) {
		//		if (tables.containsKey(cls)) {
		//			throw new IllegalStateException("Database table '" + cls.getName() + "' already exists!");
		//		}

		if (hikariSource == null) {
			initialized = true;
			start(); //Initialize connection automatically
		}

		Reflection.initialize(cls); //Call static initializer

		if (cls.getSuperclass() != Object.class && cls.getSuperclass().isAnnotationPresent(DBTable.class)) {
			createTable(cls.getSuperclass()); //Create parent first if it exists
		}
		MDBTableData data = getTableMeta(cls);
		OsmiumLogger.info("Creating " + type.getName() + " database table: " + data.getName());
		if (type == DatabaseType.MYSQL) {
			this.update(MDBUtil.getCreateTableUpdate(data));
		} else {
			update(DBUtil.createTable(data));
		}

		//		TableProperties data = new TableProperties(this, cls);
		//		tables.put(cls, data);
		//
		//		try {
		//			Class.forName(cls.getName()); //Call static initializer
		//		} catch (ClassNotFoundException e) {
		//			e.printStackTrace();
		//		}
		//
		//		update(DBUtil.createTable(this, data));

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

	}

}
