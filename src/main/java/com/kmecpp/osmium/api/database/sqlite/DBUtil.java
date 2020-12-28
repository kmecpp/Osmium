package com.kmecpp.osmium.api.database.sqlite;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import com.kmecpp.osmium.api.database.DBColumn;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;

public class DBUtil {

	public static HashMap<Class<?>, String> types = new HashMap<>();

	static {
		types.put(boolean.class, "TINYINT(1)");
		types.put(Boolean.class, "TINYINT(1)");
		types.put(byte.class, "TINYINT");
		types.put(Byte.class, "TINYINT");
		types.put(short.class, "SMALLINT");
		types.put(Short.class, "SMALLINT");
		types.put(int.class, "INT");
		types.put(Integer.class, "INT");
		types.put(long.class, "BIGINT");
		types.put(Long.class, "BIGINT");
		types.put(float.class, "FLOAT");
		types.put(Float.class, "FLOAT");
		types.put(double.class, "DOUBLE");
		types.put(Double.class, "DOUBLE");
		types.put(UUID.class, "CHAR(36)");
		types.put(Date.class, "DATE");
		types.put(Time.class, "TIME");
		types.put(Timestamp.class, "TIMESTAMP");

		//SQLITE
		types.put(String.class, "VARCHAR");
	}

	public String createWhere(SQLiteDatabase db, Class<?> cls, Object... primaryKeys) {
		String[] columns = db.getTable(cls).getPrimaryColumns();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < columns.length; i++) {
			sb.append("AND \"" + columns[0] + "\"='" + primaryKeys[0] + "'");
		}
		return sb.toString();
	}

	public static final String createTable(SQLiteDatabase db, TableProperties properties) {
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

		boolean autoIncrement = false;
		for (Field field : properties.getFields()) {
			DBColumn column = field.getAnnotation(DBColumn.class);

			try {
				if (!field.getType().isPrimitive()) {
					Class.forName(field.getType().getName()); //Call static initializer					
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//			SerializationData<?> serializationData = Serialization.getData(field.getType());
			//			if (serializationData == null) {
			//				throw new IllegalArgumentException("Cannot create database table with unregistered type: " + field.getType());
			//			}

			String typeString = types.get(field.getType());
			if (typeString == null) {
				if (Serialization.isSerializable(field.getType())) {
					typeString = "SERIALIZABLE";
				} else {
					throw new IllegalArgumentException("Cannot create database table with unregistered type: " + field.getType());
				}
			}

			if (column.autoIncrement()) {
				autoIncrement = true;
				schema.append(getColumnName(field)).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			} else {
				schema.append(getColumnName(field))
						.append(" " + typeString)
						.append(column.nullable() ? "" : " NOT NULL")
						.append(column.autoIncrement() ? " AUTOINCREMENT" : "")
						.append(column.unique() ? " UNIQUE" : "")
						.append(", ");
			}

		}

		if (properties.getPrimaryColumns().length > 0 && !autoIncrement) {
			schema.append("PRIMARY KEY("
					+ StringUtil.join(properties.getPrimaryColumns(), ", ")
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

	public static final String createReplaceInto(SQLiteDatabase db, Class<?> cls, Object obj) {
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();

		TableProperties info = db.getTable(cls);
		for (Field field : info.getFields()) {
			columns.add(DBUtil.getColumnName(field));

			Object value = Reflection.getFieldValue(obj, field);
			values.add(value == null ? null : Serialization.serializeAndQuote(value));
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
