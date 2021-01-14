package com.kmecpp.osmium.api.database.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

import com.kmecpp.osmium.api.database.DBTable;
import com.kmecpp.osmium.api.database.DatabaseType;
import com.kmecpp.osmium.api.database.OrderBy;
import com.kmecpp.osmium.api.database.ResultSetProcessor;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.mysql.MDBColumnData;
import com.kmecpp.osmium.api.database.mysql.MDBTableData;
import com.kmecpp.osmium.api.database.mysql.MDBUtil;
import com.kmecpp.osmium.api.database.sqlite.DatabaseQueue.QueueExecutor;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.IOUtil;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.core.OsmiumCoreConfig;

@DBTable(name = "f", type = DatabaseType.SQLITE)
public class SQLiteDatabase extends SQLDatabase {

	//	private final HashMap<Class<?>, TableProperties> tables = new HashMap<>();

	public SQLiteDatabase(OsmiumPlugin plugin) {
		super(plugin, DatabaseType.SQLITE);
	}

	public void configure(String prefix) {
		configure(prefix, null, -1, null, null, null);
	}

	//	public final Collection<TableProperties> getTables() {
	//		return tables.values();
	//	}

	//	public final TableProperties getTable(Class<?> tableClass) {
	//		return tables.get(tableClass);
	//	}

	public final String getTableName(Class<?> tableClass) {
		return tables.get(tableClass).getName();
	}

	@Override
	public void replaceInto(Class<?> cls, Object obj) {
		//		update(DBUtil.createReplaceInto(this, cls, obj));

		MDBTableData tableData = tables.get(cls);
		String update = MDBUtil.createReplaceInto(tableData);

		this.preparedUpdateStatement(update, s -> {
			try {
				MDBColumnData[] columns = tableData.getColumns();
				for (int i = 0; i < columns.length; i++) {
					DBUtil.updatePreparedStatement(s, i + 1, columns[i].getField().get(obj));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		//		TableProperties tableData = getTable(cls);
		//		String update = DBUtil.createReplaceInto(tableData);
		//
		//		this.preparedUpdateStatement(update, s -> {
		//			try {
		//				Field[] columns = tableData.getFields();
		//				for (int i = 0; i < columns.length; i++) {
		//					DBUtil.updatePreparedStatement(s, i + 1, columns[i].get(obj));
		//				}
		//			} catch (Exception e) {
		//				throw new RuntimeException(e);
		//			}
		//		});
	}

	//	/**
	//	 * Queues the SQL query to execute asynchronously sometime in the future.
	//	 * This method has the same effect as executeUpdate() but does not wait for
	//	 * a response, resulting in an execution time ~5µs on average, as well as
	//	 * having the benefit of not having the server hang on a slow connection
	//	 * 
	//	 * @param update
	//	 *            the SQL query to execute
	//	 */
	//	public void updateAsync(String update) {
	//		queue.queue(update);
	//	}

	/**
	 * Executes the given SQL statement which may be an INSERT, UPDATE, or
	 * DELETE statement or an SQL statement that returns nothing, such as an SQL
	 * DDL statement.
	 * 
	 * @param update
	 *            the SQL statement to execute
	 */
	@Override //TODO: Move this to superclass
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
			if (!OsmiumCoreConfig.debug) {
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
		MDBTableData properties = tables.get(tableClass);
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
		MDBTableData properties = tables.get(tableClass);
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
		MDBTableData properties = tables.get(tableClass);
		return query("SELECT * FROM " + properties.getName() + " " + orderBy + " LIMIT " + limit, properties);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, String orderBy, int min, int max) {
		return orderBy(tableClass, OrderBy.desc(orderBy), min, max);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int min, int max) {
		MDBTableData properties = tables.get(tableClass);
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
		MDBTableData table = tables.get(tableClass);
		if (columns == null) {
			columns = table.getPrimaryColumnNames();
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
	public <T> ArrayList<T> query(String query, MDBTableData properties) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			MDBColumnData[] columns = properties.getColumns();
			ArrayList<T> result = new ArrayList<>();

			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				T obj = Reflection.cast(Reflection.createInstance(properties.getTableClass()));

				for (int i = 0; i < columns.length; i++) {
					columns[i].getField().set(obj, Serialization.deserialize(properties.getColumns()[i].getType(), (resultSet.getString(i + 1))));
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
			throw new RuntimeException("Failed to execute database query: \"" + query + "\"", e);
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

	public void renameTable(String oldName, String newName) {
		update("ALTER TABLE " + oldName + " RENAME TO " + newName);
	}

}
