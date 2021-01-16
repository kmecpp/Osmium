package com.kmecpp.osmium.api.database.mysql;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.kmecpp.osmium.api.database.DBColumn;
import com.kmecpp.osmium.api.database.SQLPhrase;
import com.kmecpp.osmium.api.util.StringUtil;

public class MDBUtil {

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
	}

	public static String getTypeString(MDBTableData tableData, MDBColumnData data) {
		//		if (data.isForeignKey()) {
		//			return "INT";
		//		}
		//		System.out.println(data.isForeignKey());
		//		System.out.println(data.getType());

		Class<?> type = data.getType();

		String typeString = types.get(type);
		if (typeString != null) {
			return typeString;
		}

		int maxLength = data.getMaxLength();
		if (maxLength <= 0) {
			throw new IllegalArgumentException("Column has non-positive max length on a string field: " + tableData.getName() + "." + data.getName());
		}
		if (maxLength <= 1000) {
			return "VARCHAR(" + maxLength + ")";
		} else if (maxLength <= 65_535) {
			return "TEXT";
		} else if (maxLength <= 16_777_215) {
			return "MEDIUMTEXT";
		} else if (maxLength <= 4_294_967_295L) {
			return "LONGTEXT";
		} else {
			throw new RuntimeException("Unsupported MySQL type: " + type.getName());
		}
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
			s.setDate(index, (Date) value);
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

	public static void processResultSet(Object instance, ResultSet rs, int index, MDBColumnData column) throws Exception {
		Field field = column.getField();
		Class<?> type = field.getType();
		if (type == boolean.class || type == Boolean.class) {
			field.setBoolean(instance, rs.getBoolean(index));
		} else if (type == byte.class || type == Byte.class) {
			field.setByte(instance, rs.getByte(index));
		} else if (type == short.class || type == Short.class) {
			field.setShort(instance, rs.getShort(index));
		} else if (type == int.class || type == Integer.class) {
			field.setInt(instance, rs.getInt(index));
		} else if (type == long.class || type == Long.class) {
			field.setLong(instance, rs.getLong(index));
		} else if (type == float.class || type == Float.class) {
			field.setFloat(instance, rs.getFloat(index));
		} else if (type == double.class || type == Double.class) {
			field.setDouble(instance, rs.getDouble(index));
		} else if (type == String.class) {
			field.set(instance, rs.getString(index));
		} else if (type == UUID.class) {
			field.set(instance, UUID.fromString(rs.getString(index)));
		}
	}

	public static String getColumnAttributeString(MDBTableData tableData, MDBColumnData data) {
		StringBuilder sb = new StringBuilder();
		StringUtil.add(sb, getTypeString(tableData, data));
		StringUtil.add(sb, data.isUnique() ? "unique" : "");
		StringUtil.add(sb, data.isNullable() ? "" : "not null");
		StringUtil.add(sb, data.isAutoIncrement() ? "auto_increment" : "");
		if (data.getDefaultValue() != null) {
			Object value = data.getDefaultValue();

			if (value instanceof String || value instanceof UUID) {
				StringUtil.add(sb, "default '" + value + "'");
			} else if (value instanceof SQLPhrase) {
				StringUtil.add(sb, "default " + ((SQLPhrase) value).getPhrase());
			} else {
				StringUtil.add(sb, "default " + value);
			}
		}
		return sb.toString();
	}

	public static String getCreateTableUpdate(MDBTableData data) {
		StringBuilder sb = new StringBuilder("create table if not exists " + data.getName() + "(");
		ArrayList<String> primaryKeys = new ArrayList<>();
		//		LinkedHashMap<Class<?>, ArrayList<String>> foreignKeys = new LinkedHashMap<>();
		for (MDBColumnData column : data.getColumns()) {
			String columnName = column.getName();
			sb.append("`" + columnName + "` " + getColumnAttributeString(data, column) + ", ");

			if (column.isPrimary()) {
				primaryKeys.add(columnName);
			}
			//			if (column.isForeignKey()) {
			//				foreignKeys.computeIfAbsent(column.getType(), k -> new ArrayList<>()).add(columnName);
			//			}
		}
		if (!primaryKeys.isEmpty()) {
			sb.append("primary key(" + String.join(", ", primaryKeys) + "),");
		}
		//		for (Entry<Class<?>, ArrayList<String>> entry : foreignKeys.entrySet()) {
		//			Class<?> target = entry.getKey();
		//			MDBTableData foreignTableData = manager.getData(target);
		//
		//			ArrayList<String> keys = entry.getValue();
		//			sb.append("foreign key(" + String.join(", ", keys) + ")");
		//			sb.append("references " + foreignTableData.getTableName() + "(id) on delete cascade,");
		//		}

		if (sb.charAt(sb.length() - 1) == ',') {
			sb.setLength(sb.length() - 1);
		}
		sb.append(");");
		return sb.toString();
	}

	//	public static String createJoins(MDBTableData data) {
	//		StringBuilder sb = new StringBuilder();
	//		for (MDBColumnData column : data.getForeignKeyColumns()) {
	//			MDBTableData target = MDB.getData(column.getType());
	//			sb.append("inner join " + target.getTableName() + " on " + target.getTableName() + ".id=" + data.getTableName() + "." + column.getName());
	//		}
	//		return sb.toString();
	//	}

	public static String createWhere(String[] columns) {
		//		if (columns.length != values.length) {
		//			throw new IllegalArgumentException("Column size does not match value size: " + columns.length + " vs "
		//					+ values.length + " Columns: " + Arrays.toString(columns) + " Values: " + Arrays.toString(values));
		//		} else {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columns.length; ++i) {
			//				sb.append((i > 0 ? " AND " : "") + "" + columns[i] + "='" + values[i] + "'");
			sb.append((i > 0 ? " AND " : "") + "" + columns[i] + "=?");
		}
		return sb.toString();
		//		}
	}

	public static final String createReplaceInto(MDBTableData table) {
		return "REPLACE INTO " + table.getName()
				+ "(" + StringUtil.join(table.getColumnNames(), ", ") + ") "
				+ "VALUES(" + StringUtil.join('?', ",", table.getColumnCount()) + ");";
	}

	public static DBColumn createForeignKeyMeta() {
		class Temp {

			@DBColumn(primary = true)
			public int field;

		}
		try {
			return Temp.class.getField("field").getAnnotation(DBColumn.class);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new Error(e);
		}
	}

	//	public static MDBTableData createForeignKey(Field field) {
	//		if (field.getType().isAnnotationPresent(MySQLTable.class)) {
	//			return Require.nonNull(MDB.getData(field.getType())); //We should have registered foreign keys first
	//		}
	//		return null;
	//	}

	public static DBColumn createDefaultColumnAnnotation() {
		class Temp {

			@DBColumn
			public Object field;

		}
		try {
			return Temp.class.getField("field").getAnnotation(DBColumn.class);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new Error(e);
		}
	}

}
