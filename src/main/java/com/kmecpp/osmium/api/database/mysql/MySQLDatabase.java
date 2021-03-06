package com.kmecpp.osmium.api.database.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import com.kmecpp.osmium.api.database.Filter;
import com.kmecpp.osmium.api.database.DatabaseType;
import com.kmecpp.osmium.api.database.OrderBy;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class MySQLDatabase extends SQLDatabase {

	//	private static final ExecutorService scheduler = Executors.newFixedThreadPool(2);

	//	private String tablePrefix;

	public MySQLDatabase(OsmiumPlugin plugin) {
		super(plugin, DatabaseType.MYSQL);
	}

	//	public String getTablePrefix() {
	//		return tablePrefix;
	//	}

	/*
	 * TODO:
	 * For foreign keys need to decide what the context is. Database level? How
	 * do we pass around database instance
	 */

	//	public static void main(String[] args) {
	//		//		System.out.println(MDBUtil.getCreateTableUpdate(new MDBTableData(RewardClaimsTable.class)));
	//		System.out.println(MDBUtil.getCreateTableUpdate(new MDBTableData(ProductOrder.class)));
	//	}

	public int count(Class<?> tableClass) {
		MDBTableData table = tables.get(tableClass);
		return get("SELECT COUNT(*) FROM " + table.getName(), rs -> rs.getInt(1));
	}

	public int count(Class<?> tableClass, Filter filter) {
		MDBTableData table = tables.get(tableClass);
		String where = MDBUtil.createWhere(filter);
		return query("SELECT COUNT(*) FROM " + table.getName() + " WHERE " + where, MDBUtil.filterLinker(filter), rs -> {
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		});
	}

	public int count(Class<?> tableClass, String columns, Object... values) {
		MDBTableData table = tables.get(tableClass);
		String where = MDBUtil.createWhere(columns.split(","));
		//		return query("SELECT COUNT(*) FROM " + table.getName() + " WHERE " + where + (StringUtil.isNullOrEmpty(extraFilter) ? "" : " AND " + extraFilter), ps -> {
		return query("SELECT COUNT(*) FROM " + table.getName() + " WHERE " + where, ps -> {
			for (int i = 0; i < values.length; i++) {
				MDBUtil.updatePreparedStatement(ps, i + 1, values[i]);
			}
		}, rs -> {
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		});
	}

	public int setAll(Class<?> tableClass, String column, Object value) {
		MDBTableData table = tables.get(tableClass);
		return preparedUpdateStatement("update " + table.getName() + " set " + SQLDatabase.getColumnName(column) + "=?", ps -> MDBUtil.updatePreparedStatement(ps, 1, value));
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int limit) {
		MDBTableData table = tables.get(tableClass);
		return query(table, "SELECT * FROM " + table.getName() + " " + orderBy + " LIMIT " + limit);
	}

	public <T> ArrayList<T> orderBy(Class<T> tableClass, OrderBy orderBy, int limit, String columns, Object... values) {
		MDBTableData table = tables.get(tableClass);
		return query(table, "SELECT * FROM " + table.getName()
				+ " WHERE " + MDBUtil.createWhere(columns.split(","))
				+ " " + orderBy + " LIMIT " + limit, values);
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

		OsmiumLogger.debug("QUERY: " + query);
		ArrayList<T> results = new ArrayList<>();
		this.preparedQueryStatement(query, s -> {
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
		return results;
	}

	@Override
	public void replaceInto(Class<?> tableClass, Object obj) {
		MDBTableData tableData = tables.get(tableClass);
		String update = MDBUtil.createReplaceInto(tableData);

		this.preparedUpdateStatement(update, s -> {
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
	//	}	//	public static <T> T get(Class<T> cls) {
	//		MDBTableData data = Require.nonNull(tables.get(cls));
	//		DB.get().query("select * "+ data.getTableName() + , handler);
	//	}

	//	public MDBTableData getTableMeta(Class<?> cls) {
	//		MDBTableData data = tables.get(cls);
	//		if (data != null) {
	//			return data;
	//		}
	//		registerTable(cls);
	//		return tables.get(cls);
	//	}
	//
	//	public void registerTable(Class<?> cls) {
	//		MDBTableData data = new MDBTableData(this, cls);
	//		tables.put(cls, data);
	//	}

	//	public void createTable(Class<?> cls) {
	//		Reflection.initialize(cls); //Call static initializer
	//
	//		if (cls.getSuperclass() != Object.class && cls.getSuperclass().isAnnotationPresent(MySQLTable.class)) {
	//			createTable(cls.getSuperclass()); //Create parent first if it exists
	//		}
	//		MDBTableData data = getTableMeta(cls);
	//		OsmiumLogger.info("Creating database table: '" + data.getName() + "'");
	//		this.update(MDBUtil.getCreateTableUpdate(data));
	//	}

}
