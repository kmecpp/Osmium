package com.kmecpp.osmium.api.database;

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

import com.kmecpp.osmium.api.database.api.DBColumn;
import com.kmecpp.osmium.api.database.api.Filter;
import com.kmecpp.osmium.api.database.api.PreparedStatementBuilder;
import com.kmecpp.osmium.api.database.api.SQL;
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
	}

	public static String getTypeString(TableData tableData, ColumnData data) {
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
			throw new UnsupportedOperationException("SQL serialization of " + value.getClass().getSimpleName() + " (" + value + ") is not supported yet!");
		}
	}

	public static void processResultSet(Object instance, ResultSet rs, int index, ColumnData column) throws Exception {
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
		} else if (type == Date.class) {
			field.set(instance, rs.getDate(index));
		} else if (type == Time.class) {
			field.set(instance, rs.getTime(index));
		} else if (type == Timestamp.class) {
			field.set(instance, rs.getTimestamp(index));
		} else if (type == String.class) {
			field.set(instance, rs.getString(index));
		} else if (type == UUID.class) {
			field.set(instance, UUID.fromString(rs.getString(index)));
		}
	}

	public static PreparedStatementBuilder filterLinker(Filter filter) {
		return ps -> filter.link(ps);
	}

	public static String getColumnAttributeString(TableData tableData, ColumnData data) {
		StringBuilder sb = new StringBuilder();
		StringUtil.add(sb, getTypeString(tableData, data));
		StringUtil.add(sb, data.isUnique() ? "unique" : "");
		StringUtil.add(sb, data.isNullable() ? "" : "not null");
		StringUtil.add(sb, data.isAutoIncrement() ? "auto_increment" : "");
		if (data.getDefaultValue() != null && !data.getDefaultValue().equals(SQL.NULL)) {
			Object value = data.getDefaultValue();

			if (data.getType() == String.class || data.getType() == UUID.class) {
				StringUtil.add(sb, "default '" + value + "'");
			} else {
				StringUtil.add(sb, "default " + value);
			}
		}

		if (tableData.isMySQL() && data.getType() == Timestamp.class && data.getDefaultValue() == null) {
			StringUtil.add(sb, "default 0");
		}

		return sb.toString();
	}

	public static String getCreateTableUpdate(TableData data) {
		StringBuilder sb = new StringBuilder("create table if not exists " + data.getName() + "(");
		ArrayList<String> primaryKeys = new ArrayList<>();
		//		LinkedHashMap<Class<?>, ArrayList<String>> foreignKeys = new LinkedHashMap<>();

		//create table if not exists TABLE(`col1` varchar(32) not null, `col2`, primary key(col1, col2));
		for (ColumnData column : data.getColumns()) {
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
			sb.append("primary key(" + String.join(", ", primaryKeys) + "), ");
		}
		//		for (Entry<Class<?>, ArrayList<String>> entry : foreignKeys.entrySet()) {
		//			Class<?> target = entry.getKey();
		//			MDBTableData foreignTableData = manager.getData(target);
		//
		//			ArrayList<String> keys = entry.getValue();
		//			sb.append("foreign key(" + String.join(", ", keys) + ")");
		//			sb.append("references " + foreignTableData.getTableName() + "(id) on delete cascade,");
		//		}

		if (sb.charAt(sb.length() - 1) == ' ' && sb.charAt(sb.length() - 2) == ',') {
			sb.setLength(sb.length() - 2);
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

	public static String createWhere(Filter filter) {
		StringBuilder sb = new StringBuilder();
		ArrayList<String> filters = filter.getFilters();
		for (int i = 0; i < filters.size(); ++i) {
			sb.append((i > 0 ? " AND " : "") + "" + filters.get(i) + "?");
		}
		return sb.toString();
	}

	public static final String createReplaceInto(TableData table) {
		return "REPLACE INTO " + table.getName()
				+ "(" + StringUtil.join(table.getEscapedColumnNames(), ", ") + ") "
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
