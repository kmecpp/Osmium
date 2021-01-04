package com.kmecpp.osmium.api.database.sqlite;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import com.kmecpp.osmium.api.database.DBColumn;
import com.kmecpp.osmium.api.database.mysql.MDBColumnData;
import com.kmecpp.osmium.api.database.mysql.MDBTableData;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.util.StringUtil;

public class DBUtil {

	public static HashMap<Class<?>, String> types = new HashMap<>();

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

	public static void updatePreparedStatement(PreparedStatement s, int index, Object value) throws SQLException {
		if (value == null) {
			s.setObject(index, null);
		} else if (value instanceof Boolean) {
			s.setBoolean(index, (boolean) value);
		} else if (value instanceof Byte) {
			s.setByte(index, (byte) value);
		} else if (value instanceof Short) {
			s.setShort(index, (short) value);
		} else if (value instanceof Integer) {
			s.setInt(index, (int) value);
		} else if (value instanceof Long) {
			s.setLong(index, (long) value);
		} else if (value instanceof Float) {
			s.setFloat(index, (float) value);
		} else if (value instanceof Double) {
			s.setDouble(index, (double) value);
		} else if (value instanceof Date) {
			s.setString(index, dateFormat.format((Date) value)); //SQLITE
		} else if (value instanceof Time) {
			s.setTime(index, (Time) value);
		} else if (value instanceof Timestamp) {
			s.setTimestamp(index, (Timestamp) value);
		} else if (value instanceof String) {
			s.setString(index, (String) value);
		} else if (value instanceof UUID) {
			s.setString(index, String.valueOf(value));
		} else {
			throw new UnsupportedOperationException("MySQL serialization of '" + value + "' is not yet supported");
		}
	}

	public String createWhere(SQLiteDatabase db, Class<?> cls, Object... primaryKeys) {
		String[] columns = db.getTableMeta(cls).getPrimaryColumnNames();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < columns.length; i++) {
			sb.append("AND \"" + columns[0] + "\"='" + primaryKeys[0] + "'");
		}
		return sb.toString();
	}

	public static final String createTable(MDBTableData properties) {
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
		for (MDBColumnData columnData : properties.getColumns()) {
			Field field = columnData.getField();
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

	//	public static final String createReplaceInto(SQLiteDatabase db, Class<?> cls, Object obj) {
	//		ArrayList<String> columns = new ArrayList<>();
	//		ArrayList<String> values = new ArrayList<>();
	//
	//		TableProperties info = db.getTable(cls);
	//		for (Field field : info.getFields()) {
	//			columns.add(DBUtil.getColumnName(field));
	//
	//			Object value = Reflection.getFieldValue(obj, field);
	//			values.add(value == null ? null : Serialization.serializeAndQuote(value));
	//		}
	//
	//		return "REPLACE INTO " + info.getName()
	//				+ "(" + StringUtil.join(columns, ", ") + ") "
	//				+ "VALUES(" + StringUtil.join(values, ", ") + ");";
	//	}

	public static final String createReplaceInto(MDBTableData table) {
		return "REPLACE INTO " + table.getName()
				+ "(" + StringUtil.join(table.getColumns(), ", ") + ") "
				+ "VALUES(" + StringUtil.join('?', ",", table.getColumnCount()) + ");";
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
