package com.kmecpp.osmium.api.database.mysql;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;

public class MDBTableData {

	private String name;
	private Class<?> tableClass;
	private LinkedHashMap<String, MDBColumnData> columnMap;

	private String[] columnNames;
	private MDBColumnData[] columns;

	private MDBColumnData[] primaryColumns;
	private String[] primaryColumnNames;

	//	private MDBColumnData[] foreignKeyColumns;
	//	private String[] foreignKeyColumnNames;

	public MDBTableData(MySQLDatabase database, Class<?> cls) {
		MySQLTable meta = cls.getDeclaredAnnotation(MySQLTable.class);
		this.tableClass = cls;
		this.columnMap = new LinkedHashMap<>();

		if (meta == null) {
			throw new IllegalArgumentException("Missing @" + MySQLTable.class.getSimpleName() + " annotation");
		}

		this.name = (StringUtil.isNullOrEmpty(database.getTablePrefix()) ? "" : database.getTablePrefix() + "_") + meta.name();

		//		MDBTableData parentData = manager.getParentData(cls);
		//		if (parentData != null) {
		//			MDBColumnData column = MDBColumnData.createForeignKeyData(manager, parentData.getTableClass());
		//			this.columnMap.put(column.getName(), column);
		//		}

		ArrayList<MDBColumnData> columns = new ArrayList<>();
		ArrayList<MDBColumnData> primaryColumns = new ArrayList<>();
		//		ArrayList<MDBColumnData> foreignKeyColumns = new ArrayList<>();
		Reflection.walk(cls, false, field -> {
			MDBColumnData columnData = new MDBColumnData(field);
			this.columnMap.put(columnData.getName(), columnData);

			columns.add(columnData);
			if (columnData.isPrimary()) {
				primaryColumns.add(columnData);
			}
			//			if (columnData.isForeignKey()) {
			//				foreignKeyColumns.add(columnData);
			//			}
		});
		this.columns = columns.toArray(new MDBColumnData[columns.size()]);
		this.columnNames = columns.stream().map(MDBColumnData::getName).toArray(String[]::new);
		this.primaryColumns = primaryColumns.toArray(new MDBColumnData[primaryColumns.size()]);
		this.primaryColumnNames = primaryColumns.stream().map(MDBColumnData::getName).toArray(String[]::new);
		//		this.foreignKeyColumns = foreignKeyColumns.toArray(new MDBColumnData[foreignKeyColumns.size()]);
		//		this.foreignKeyColumnNames = foreignKeyColumns.stream().map(MDBColumnData::getName).toArray(String[]::new);
	}

	public Class<?> getTableClass() {
		return tableClass;
	}

	public String getName() {
		return name;
	}

	public LinkedHashMap<String, MDBColumnData> getColumnMap() {
		return columnMap;
	}

	public MDBColumnData[] getColumns() {
		return columns;
	}

	public int getColumnCount() {
		return columns.length;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public MDBColumnData[] getPrimaryColumns() {
		return primaryColumns;
	}

	public String[] getPrimaryColumnNames() {
		return primaryColumnNames;
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
