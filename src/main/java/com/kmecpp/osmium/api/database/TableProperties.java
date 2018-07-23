package com.kmecpp.osmium.api.database;

import java.lang.reflect.Field;

import com.kmecpp.osmium.util.Reflection;

public class TableProperties {

	private Class<?> tableClass;
	private String name;

	private String[] columns;
	private String[] primaryColumns;

	private Field[] fields;
	private Field[] primaryFields;

	public TableProperties(Class<?> cls) {
		this.tableClass = cls;
		this.name = cls.getAnnotation(DBTable.class).name();

		for (Field field : Reflection.getFieldsWith(cls, DBColumn.class)) {
			field.getName();
		}
		this.columns = DBUtil.getColumns(cls);
		this.primaryColumns = DBUtil.getPrimaryColumns(cls);
		this.fields = DBUtil.getFields(cls);
		this.primaryFields = DBUtil.getPrimaryFields(cls);
	}

	public String getName() {
		return name;
	}

	public int getColumnCount() {
		return columns.length;
	}

	public String[] getColumns() {
		return columns;
	}

	public String[] getPrimaryColumns() {
		return primaryColumns;
	}

	public Field[] getFields() {
		return fields;
	}

	public Field[] getPrimaryFields() {
		return primaryFields;
	}

	public Class<?> getTableClass() {
		return tableClass;
	}

}
