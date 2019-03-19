package com.kmecpp.osmium.api.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.persistence.SerializationData;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;
import com.kmecpp.osmium.core.CoreOsmiumConfig;

public class DBUtil {

	public String createWhere(Database db, Class<?> cls, Object... primaryKeys) {
		String[] columns = db.getTable(cls).getPrimaryColumns();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < columns.length; i++) {
			sb.append("AND \"" + columns[0] + "\"='" + primaryKeys[0] + "'");
		}
		return sb.toString();
	}

	public static final String createTable(Database db, TableProperties properties) {
		if (properties.getColumnCount() == 0) {
			throw new IllegalArgumentException("Invalid database table '" + properties.getName() + "' Must contain at least one column!");
		}

		StringBuilder schema = new StringBuilder("CREATE TABLE IF NOT EXISTS " + properties.getName() + " (");

		//		if (CoreOsmiumConfig.Database.enableMysql) {
		//			String[] primaryColumnLengths = new String[];
		//
		//			for (Field field : properties.getFields()) {
		//				DBColumn column = field.getAnnotation(DBColumn.class);
		//
		//				schema.append(getColumnName(field))
		//						.append(" " + db.getSerializationData(field.getType()).getType().getName())
		//						.append(column.notNull() ? " NOT NULL" : "")
		//						.append(column.autoIncrement() ? " AUTOINCREMENT" : "")
		//						.append(column.unique() ? " UNIQUE" : "")
		//						.append(", ");
		//				if (column.primary()) {
		//
		//				}
		//			}
		//
		//			if (properties.getPrimaryColumns().length > 0) {
		//				schema.append("PRIMARY KEY(" + StringUtil.join(properties.getPrimaryColumns(), ", ") + ")");
		//			} else {
		//				schema.setLength(schema.length() - 2);
		//			}
		//		} else {
		//
		//		}

		for (Field field : properties.getFields()) {
			DBColumn column = field.getAnnotation(DBColumn.class);

			SerializationData<?> serializationData = Serialization.getData(field.getType());
			if (serializationData == null) {
				throw new IllegalArgumentException("Cannot create database table with unregistered type: " + field.getType());
			}

			schema.append(getColumnName(field))
					.append(" " + serializationData.getType().getName())
					.append(column.notNull() ? " NOT NULL" : "")
					.append(column.autoIncrement() ? " AUTOINCREMENT" : "")
					.append(column.unique() ? " UNIQUE" : "")
					.append(", ");
		}

		if (properties.getPrimaryColumns().length > 0) {
			schema.append("PRIMARY KEY("
					+ StringUtil.join(CoreOsmiumConfig.Database.useMySql ? properties.getPrimaryColumnsWithLengths() : properties.getPrimaryColumns(), ", ")
					+ ")");
		} else {
			schema.setLength(schema.length() - 2);
		}

		return schema.append(");").toString();
	}

	public static String createWhere(String[] columns, Object... values) {
		if (columns.length != values.length) {
			throw new IllegalArgumentException("Column size does not match value size: " + columns.length + " vs " + values.length
					+ " Columns: " + Arrays.toString(columns) + " Values: " + Arrays.toString(values));
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columns.length; i++) {
			sb.append((i > 0 ? " AND " : "") + "" + columns[i] + "='" + values[i] + "'");
		}
		return sb.toString();
	}

	public static final String createReplaceInto(Database db, Class<?> cls, Object obj) {
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();

		TableProperties info = db.getTable(cls);
		for (Field field : info.getFields()) {
			columns.add(DBUtil.getColumnName(field));

			Object value = Reflection.getFieldValue(obj, field);
			values.add(value == null ? null : "\"" + Serialization.serialize(value) + "\"");
		}

		return "REPLACE INTO " + info.getName()
				+ "(" + StringUtil.join(columns, ", ") + ") "
				+ "VALUES(" + StringUtil.join(values, ", ") + ");";
	}

	public static String getColumnName(Field field) {
		return getColumnName(field.getName());

	}

	public static String getColumnName(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			sb.append(Character.isUpperCase(c) ? "_" + Character.toLowerCase(c) : c);
		}
		return sb.toString();
	}

}
