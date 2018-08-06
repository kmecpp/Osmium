package com.kmecpp.osmium.api.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.kmecpp.osmium.api.util.Reflection;

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
		ArrayList<Field> fields = new ArrayList<>();
		ArrayList<Field> primaryFields = new ArrayList<>();
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> primaryColumns = new ArrayList<>();

		for (Field field : cls.getDeclaredFields()) {
			field.setAccessible(true);
			DBColumn annotation = field.getAnnotation(DBColumn.class);
			if (annotation != null && !Modifier.isStatic(field.getModifiers())) {
				String name = DBUtil.getColumnName(field);
				fields.add(field);
				columns.add(name);
				if (annotation.primary()) {
					primaryFields.add(field);
					primaryColumns.add(name);
				}
			}
		}
		this.fields = fields.toArray(new Field[fields.size()]);
		this.primaryFields = primaryFields.toArray(new Field[primaryFields.size()]);
		this.columns = columns.toArray(new String[columns.size()]);
		this.primaryColumns = primaryColumns.toArray(new String[primaryColumns.size()]);

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

	@SuppressWarnings("unchecked")
	public <T> Class<T> getTableClass() {
		return (Class<T>) tableClass;
	}

}
