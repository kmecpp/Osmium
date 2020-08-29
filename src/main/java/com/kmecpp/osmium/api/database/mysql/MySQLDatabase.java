package com.kmecpp.osmium.api.database.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.kmecpp.osmium.api.database.OrderBy;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Completer;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;

public class MySQLDatabase extends SQLDatabase {

	private static final ExecutorService scheduler = Executors.newFixedThreadPool(2);

	private final HashMap<Class<?>, MDBTableData> tables = new HashMap<>();

	private OsmiumPlugin plugin;
	private String tablePrefix;

	public MySQLDatabase(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void initialize(String host, int port, String database, String username, String password) {
		HikariConfig config = new HikariConfig();

		if (StringUtil.isNullOrEmpty(database)) {
			throw new IllegalArgumentException("Cannot initializing empty database for plugin: " + plugin.getName());
		}

		//		OsmiumLogger.error("INITIALIZING MYSQL DATABASE WITH PREFIX: " + tablePrefix);

		try {
			OsmiumLogger.info("Using MySQL for database storage");

			config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
			config.setDriverClassName("com.mysql.jdbc.Driver");
			config.setUsername(username);
			config.setPassword(password);
			config.setConnectionTestQuery("USE " + database);

			config.setMinimumIdle(2);
			config.setMaximumPoolSize(10);
			config.setConnectionTimeout(500L);
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

	public void query(String query, Consumer<ResultSet> handler) {
		get(query, rs -> {
			handler.accept(rs);
			return null;
		});
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

	/*
	 * TODO:
	 * For foreign keys need to decide what the context is. Database level? How
	 * do we pass around database instance
	 */

	//	public static void main(String[] args) {
	//		//		System.out.println(MDBUtil.getCreateTableUpdate(new MDBTableData(RewardClaimsTable.class)));
	//		System.out.println(MDBUtil.getCreateTableUpdate(new MDBTableData(ProductOrder.class)));
	//	}

	public int setAll(Class<?> tableClass, String column, Object value) {
		MDBTableData table = tables.get(tableClass);
		return preparedStatement("update " + table.getName() + " set " + SQLDatabase.getColumnName(column) + "=?", ps -> MDBUtil.updatePreparedStatement(ps, 1, value));
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int limit) {
		MDBTableData table = tables.get(tableClass);
		return query(table, "SELECT * FROM " + table.getName() + " " + orderBy + " LIMIT " + limit);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int min, int max) {
		MDBTableData table = tables.get(tableClass);
		return query(table, "SELECT * FROM " + table.getName() + " " + orderBy + " LIMIT " + min + "," + max);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, String orderBy, int min, int max) {
		return orderBy(tableClass, OrderBy.desc(orderBy), min, max);
	}

	public <T> Optional<T> getFirst(Class<T> tableClass, OrderBy orderBy, String columns, Object... values) {
		MDBTableData table = tables.get(tableClass);
		ArrayList<T> result = query(table, "SELECT * FROM " + table.getName()
				+ " WHERE " + MDBUtil.createWhere(columns.split(","))
				+ " " + orderBy + " LIMIT 1", values);
		return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
	}

	@Override
	public <T> ArrayList<T> query(Class<T> tableClass, String[] columns, Object... values) {
		MDBTableData tableData = tables.get(tableClass);
		if (columns == null) {
			columns = tableData.getPrimaryColumnNames();
		}

		String query = "SELECT * FROM " + tableData.getName() + " WHERE " + MDBUtil.createWhere(columns);
		return query(tableData, query, values);

		//		//		DB.get().preparedStatement("", s ->{});
		//		//		String query = "SELECT * FROM " + tableData.getTableName() + " " + MDBUtil.createJoins(tableData) + " WHERE " + MDBUtil.createWhere(columns, values);
		//		String query = "SELECT * FROM " + tableData.getName() + " WHERE " + MDBUtil.createWhere(columns);
		//		OsmiumLogger.warn("EXECUTE: " + query);
		//		ArrayList<T> results = new ArrayList<>();
		//		this.preparedStatement(query, s -> {
		//			for (int i = 0; i < values.length; i++) {
		//				MDBUtil.updatePreparedStatement(s, i + 1, values[i]);
		//			}
		//		}, rs -> {
		//			try {
		//				//				if (!rs.isBeforeFirst()) {
		//				//					//Empty
		//				//				}
		//				while (rs.next()) {
		//					T obj = Reflection.createInstance(tableClass);
		//					int index = 1;
		//					if (rs.getMetaData().getColumnCount() != tableData.getColumnCount()) {
		//						throw new SQLException("Column count mismatch. Database: " + rs.getMetaData().getColumnCount() + " class: " + tableData.getColumnCount());
		//					}
		//					for (MDBColumnData column : tableData.getColumns()) {
		//						//						if (!column.isForeignKey()) {
		//						MDBUtil.processResultSet(obj, rs, index, column);
		//						//						}
		//						index++;
		//					}
		//					//					for (MDBColumnData foreignKeyColumn : tableData.getForeignKeyColumns()) {
		//					//						//TODO: Can't have foreign key to foreign key. Make this recursive?
		//					//						for (MDBColumnData foreignObjectColumn : foreignKeyColumn.getForeignKey().getColumns()) {
		//					//							MDBUtil.processResultSet(obj, rs, index, foreignObjectColumn);
		//					//							index++;
		//					//						}
		//					//					}
		//					results.add(obj);
		//				}
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		});
		//		System.out.println("RESULTS: " + results);
		//		return results;
	}

	public <T> ArrayList<T> query(MDBTableData table, String query, Object... values) {
		//		MDBTableData tableData = tables.get(tableClass);
		//		if (columns == null) {
		//			columns = tableData.getPrimaryColumnNames();
		//		}

		OsmiumLogger.warn("EXECUTE: " + query);
		ArrayList<T> results = new ArrayList<>();
		this.preparedStatement(query, s -> {
			for (int i = 0; i < values.length; i++) {
				MDBUtil.updatePreparedStatement(s, i + 1, values[i]);
			}
		}, rs -> {
			try {
				//				if (!rs.isBeforeFirst()) {
				//					//Empty
				//				}
				while (rs.next()) {
					T obj = Reflection.createInstance(Reflection.cast(table.getTableClass()));
					int index = 1;
					if (rs.getMetaData().getColumnCount() != table.getColumnCount()) {
						throw new SQLException("Column count mismatch. Database: " + rs.getMetaData().getColumnCount() + " class: " + table.getColumnCount());
					}
					for (MDBColumnData column : table.getColumns()) {
						//						if (!column.isForeignKey()) {
						MDBUtil.processResultSet(obj, rs, index, column);
						//						}
						index++;
					}
					//					for (MDBColumnData foreignKeyColumn : tableData.getForeignKeyColumns()) {
					//						//TODO: Can't have foreign key to foreign key. Make this recursive?
					//						for (MDBColumnData foreignObjectColumn : foreignKeyColumn.getForeignKey().getColumns()) {
					//							MDBUtil.processResultSet(obj, rs, index, foreignObjectColumn);
					//							index++;
					//						}
					//					}
					results.add(obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		System.out.println("RESULTS: " + results);
		return results;
	}

	@Override
	public void replaceInto(Class<?> tableClass, Object obj) {
		MDBTableData tableData = tables.get(tableClass);
		String update = MDBUtil.createReplaceInto(tableData);

		this.preparedStatement(update, s -> {
			try {
				MDBColumnData[] columns = tableData.getColumns();
				for (int i = 0; i < columns.length; i++) {
					MDBUtil.updatePreparedStatement(s, i + 1, columns[i].getField().get(obj));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	//	public MDBTableData getParentData(Class<?> cls) {
	//		return tables.get(cls.getSuperclass());
	//	}

	public MDBTableData getData(Class<?> cls) {
		MDBTableData data = tables.get(cls);
		if (data != null) {
			return data;
		}
		registerTable(cls);
		return tables.get(cls);
	}

	//	public static <T> T get(Class<T> cls) {
	//		MDBTableData data = Require.nonNull(tables.get(cls));
	//		DB.get().query("select * "+ data.getTableName() + , handler);
	//	}

	public void registerTable(Class<?> cls) {
		MDBTableData data = new MDBTableData(this, cls);
		tables.put(cls, data);
	}

	public void createTable(Class<?> cls) {
		if (cls.getSuperclass() != Object.class && cls.getSuperclass().isAnnotationPresent(MySQLTable.class)) {
			createTable(cls.getSuperclass()); //Create parent first if it exists
		}
		MDBTableData data = getData(cls);
		this.update(MDBUtil.getCreateTableUpdate(data));
	}

}
