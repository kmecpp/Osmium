package com.kmecpp.osmium.api.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;

import com.kmecpp.osmium.api.database.api.DBTable;
import com.kmecpp.osmium.api.database.api.DatabaseType;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;

public class TableData {

	private String name;
	private Class<?> tableClass;
	private LinkedHashMap<String, ColumnData> columnMap;

	private String[] columnNames;
	private String[] escapedColumnNames;
	private ColumnData[] columns;

	private ColumnData[] primaryColumns;
	private String[] primaryColumnNames;

	private DatabaseType[] types;
	private boolean sqlite;
	private boolean mysql;

	//	private MDBColumnData[] foreignKeyColumns;
	//	private String[] foreignKeyColumnNames;

	public TableData(SQLDatabase database, Class<?> cls) {
		DBTable meta = cls.getDeclaredAnnotation(DBTable.class);
		this.tableClass = cls;
		this.columnMap = new LinkedHashMap<>();

		if (meta == null) {
			throw new IllegalArgumentException("Database table is missing @" + DBTable.class.getSimpleName() + " annotation");
		}

		EnumSet<DatabaseType> typeSet = EnumSet.copyOf(Arrays.asList(meta.type()));
		this.types = typeSet.toArray(new DatabaseType[0]);
		this.sqlite = typeSet.contains(DatabaseType.SQLITE);
		this.mysql = typeSet.contains(DatabaseType.MYSQL);

		this.name = (StringUtil.isNullOrEmpty(database.getTablePrefix()) ? "" : database.getTablePrefix() + "_") + meta.name();

		//		MDBTableData parentData = manager.getParentData(cls);
		//		if (parentData != null) {
		//			MDBColumnData column = MDBColumnData.createForeignKeyData(manager, parentData.getTableClass());
		//			this.columnMap.put(column.getName(), column);
		//		}

		ArrayList<ColumnData> columns = new ArrayList<>();
		ArrayList<ColumnData> primaryColumns = new ArrayList<>();
		//		ArrayList<MDBColumnData> foreignKeyColumns = new ArrayList<>();
		Reflection.walk(cls, false, field -> {
			ColumnData columnData = new ColumnData(field);
			this.columnMap.put(columnData.getName(), columnData);

			columns.add(columnData);
			if (columnData.isPrimary()) {
				primaryColumns.add(columnData);
			}
			//			if (columnData.isForeignKey()) {
			//				foreignKeyColumns.add(columnData);
			//			}
		});
		this.columns = columns.toArray(new ColumnData[columns.size()]);
		this.columnNames = columns.stream().map(ColumnData::getName).toArray(String[]::new);
		this.escapedColumnNames = columns.stream().map(data -> "`" + data.getName() + "`").toArray(String[]::new);
		this.primaryColumns = primaryColumns.toArray(new ColumnData[primaryColumns.size()]);
		this.primaryColumnNames = primaryColumns.stream().map(ColumnData::getName).toArray(String[]::new);
		//		this.foreignKeyColumns = foreignKeyColumns.toArray(new MDBColumnData[foreignKeyColumns.size()]);
		//		this.foreignKeyColumnNames = foreignKeyColumns.stream().map(MDBColumnData::getName).toArray(String[]::new);
	}

	public Class<?> getTableClass() {
		return tableClass;
	}

	public DatabaseType[] getTypes() {
		return types;
	}

	public String getName() {
		return name;
	}

	public ColumnData getColumnMeta(String columnName) {
		return columnMap.get(SQLDatabase.getColumnName(columnName));
	}

	public void setDefaultValue(String column, String value) {
		getColumnMeta(column).setDefaultValue(value);
	}

	public LinkedHashMap<String, ColumnData> getColumnMap() {
		return columnMap;
	}

	public ColumnData[] getColumns() {
		return columns;
	}

	public int getColumnCount() {
		return columns.length;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String[] getEscapedColumnNames() {
		return escapedColumnNames;
	}

	public ColumnData[] getPrimaryColumns() {
		return primaryColumns;
	}

	public String[] getPrimaryColumnNames() {
		return primaryColumnNames;
	}

	public boolean isMySQL() {
		return mysql;
	}

	public boolean isSQLite() {
		return sqlite;
	}

	@Override
	public String toString() {
		return "TableData[name: " + name + "]";
	}

	//	public MDBColumnData[] getForeignKeyColumns() {
	//		return foreignKeyColumns;
	//	}
	//
	//	public String[] getForeignKeyColumnNames() {
	//		return foreignKeyColumnNames;
	//	}

}
