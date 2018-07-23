package com.kmecpp.osmium.api.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;

public class DBUtil {

	public static Field[] getFields(Class<?> cls) {
		ArrayList<Field> fields = new ArrayList<>();
		for (Field field : cls.getDeclaredFields()) {
			field.setAccessible(true);
			if (isValidField(field)) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[0]);
	}

	public static Field[] getPrimaryFields(Class<?> cls) {
		ArrayList<Field> fields = new ArrayList<>();
		for (Field field : cls.getDeclaredFields()) {
			field.setAccessible(true);
			if (isValidField(field) && field.getAnnotation(DBColumn.class).primary()) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[0]);
	}

	public static String[] getColumns(Class<?> cls) {
		ArrayList<String> columns = new ArrayList<>();
		for (Field field : cls.getDeclaredFields()) {
			field.setAccessible(true);
			if (isValidField(field)) {
				columns.add(getColumnName(field));
			}
		}
		return columns.toArray(new String[0]);
	}

	public static String[] getPrimaryColumns(Class<?> cls) {
		ArrayList<String> columns = new ArrayList<>();
		for (Field field : Reflection.getFields(cls)) {
			if (isValidField(field) && field.getAnnotation(DBColumn.class).primary()) {
				columns.add(getColumnName(field));
			}
		}
		return columns.toArray(new String[0]);
	}

	public String createWhere(Class<?> cls, Object... primaryKeys) {
		String[] columns = Database.getTable(cls).getPrimaryColumns();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < columns.length; i++) {
			sb.append("AND \"" + columns[0] + "\"='" + primaryKeys[0] + "'");
		}
		return sb.toString();
	}

	public static final String createTable(TableProperties properties) {
		StringBuilder schema = new StringBuilder("CREATE TABLE IF NOT EXISTS " + properties.getName() + " (");

		for (Field field : properties.getFields()) {
			DBColumn column = field.getAnnotation(DBColumn.class);
			schema.append(getColumnName(field))
					.append(" " + DBType.getTypeName(field.getType()))
					.append(column.notNull() ? " NOT NULL" : "")
					.append(column.autoIncrement() ? " AUTOINCREMENT" : "")
					.append(column.unique() ? " UNIQUE" : "")
					.append(", ");
		}

		if (properties.getPrimaryColumns().length > 0) {
			schema.append("PRIMARY KEY(" + StringUtil.join(properties.getPrimaryColumns(), ", ") + ")");
		} else {
			schema.setLength(schema.length() - 2);
		}

		return schema.append(");").toString();
	}

	public static String createWhere(String[] columns, Object... values) {
		if (columns.length != values.length) {
			throw new IllegalArgumentException("Column size does not match value size! Columns: " + columns.length + " Values: " + values.length);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columns.length; i++) {
			sb.append((i > 0 ? " AND " : "") + "" + columns[i] + "='" + values[i] + "'");
		}
		return sb.toString();
	}

	public static final String createReplaceInto(Class<?> cls, Object obj) {
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();

		TableProperties info = Database.getTable(cls);
		for (Field field : info.getFields()) {
			columns.add(DBUtil.getColumnName(field));

			Object value = Reflection.getFieldValue(obj, field);
			values.add(value == null ? null : "\"" + Database.serialize(value) + "\"");
		}

		return "REPLACE INTO " + info.getName()
				+ "(" + StringUtil.join(columns, ", ") + ") "
				+ "VALUES(" + StringUtil.join(values, ", ") + ");";
	}

	public static boolean isValidField(Field field) {
		return !Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(DBColumn.class);
	}

	public static String getColumnName(Field field) {
		return getColumnName(field.getName());
	}

	public static String getColumnName(String name) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			sb.append(Character.isUpperCase(c) ? "_" + Character.toLowerCase(c) : c);
		}
		return sb.toString();
	}

}
