//package com.kmecpp.osmium.api.database;
//
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.kmecpp.osmium.api.util.ArrayUtil;
//import com.kmecpp.osmium.api.util.StringUtil;
//
//public class DBResult {
//
//	private ArrayList<String> columns = new ArrayList<>();
//	private ArrayList<DBRow> rows = new ArrayList<>();
//
//	public DBResult(ResultSet rs) throws SQLException {
//		ResultSetMetaData metadata = rs.getMetaData();
//		int columns = metadata.getColumnCount();
//
//		if (columns == 0) {
//			return; //Empty
//		}
//
//		DBType[] types = new DBType[columns];
//		for (int i = 1; i <= columns; i++) {
//			this.columns.add(metadata.getColumnName(i));
//			types[i - 1] = DBType.fromName(metadata.getColumnTypeName(i));
//		}
//
//		while (rs.next()) {
//			ArrayList<DBValue> data = new ArrayList<DBValue>();
//			for (int i = 1; i <= columns; i++) {
//				DBType type = types[i - 1];
//				data.add(new DBValue(type, type.get(rs, i)));
//			}
//			this.rows.add(new DBRow(this.columns, data));
//		}
//	}
//
//	public List<String> getColumns() {
//		return columns;
//	}
//
//	public ArrayList<DBRow> getRows() {
//		return rows;
//	}
//
//	public <T> ArrayList<T> as(Class<T> cls) {
//		ArrayList<T> list = new ArrayList<>();
//		for (DBRow row : rows) {
//			list.add(row.as(cls));
//		}
//		return list;
//	}
//
//	public boolean isEmpty() {
//		return rows.isEmpty();
//	}
//
//	public int size() {
//		return rows.size();
//	}
//
//	public DBRow first(DBRow def) {
//		return !rows.isEmpty() ? rows.get(0) : def;
//	}
//
//	public DBRow first() {
//		if (!rows.isEmpty()) {
//			return rows.get(0);
//		}
//		throw new RuntimeException("Query result is empty!");
//	}
//
//	/**
//	 * Gets the only row in the result. If there are multiple rows or none this
//	 * method will throw an exception.
//	 * 
//	 * @return the result's only row
//	 */
//	public DBRow only() {
//		if (!isUnique()) {
//			throw new RuntimeException("Could not retrieve only row! Found: " + size());
//		}
//		return first();
//	}
//
//	public boolean isUnique() {
//		return rows.size() == 1;
//	}
//
//	public DBRow get(int index) {
//		return rows.get(index);
//	}
//
//	public DBValue[][] getData() {
//		DBValue[][] data = new DBValue[rows.size()][columns.size()];
//		for (int i = 0; i < rows.size(); i++) {
//			data[i] = rows.get(i).getData();
//		}
//		return data;
//	}
//
//	public String[][] getDataAsString() {
//		String[][] data = new String[rows.size()][columns.size()];
//		for (int r = 0; r < rows.size(); r++) {
//			DBRow row = rows.get(r);
//			for (int c = 0; c < row.size(); c++) {
//				data[r][c] = row.get(c).toString();
//			}
//		}
//		return data;
//	}
//
//	@Override
//	public String toString() { //This was annoying
//		if (rows.isEmpty()) {
//			return "";
//		}
//		StringBuilder sb = new StringBuilder();
//
//		String[][] table = getDataAsString();
//		String[][] transpose = ArrayUtil.transpose(table);
//		int[] columnSizes = new int[transpose.length];
//
//		//Build separator
//		StringBuilder separator = new StringBuilder();
//		for (int i = 0; i < columns.size(); i++) {
//			columnSizes[i] = Math.max(columns.get(i).length(), StringUtil.longestLength(transpose[i])); //Compensate for padding
//			separator.append((i == 0 ? "+" : "") + StringUtil.repeat('-', columnSizes[i] + 2) + "+");
//		}
//
//		//Resize
//		String[] resizedColumns = new String[columns.size()];
//		String[][] resizedData = new String[table.length][table[0].length];
//
//		for (int i = 0; i < columns.size(); i++) {
//			resizedColumns[i] = StringUtil.ensureLength(columns.get(i), columnSizes[i]);
//		}
//		for (int row = 0; row < table.length; row++) {
//			for (int col = 0; col < table[row].length; col++) {
//				resizedData[row][col] = StringUtil.ensureLength(table[row][col], columnSizes[col], -1);
//			}
//		}
//
//		//Print column header
//		sb.append(separator + System.lineSeparator()
//				+ ("| " + StringUtil.join(resizedColumns, " | ") + " |") + System.lineSeparator()
//				+ separator + System.lineSeparator());
//
//		//Print data
//		for (int row = 0; row < table.length; row++) {
//			sb.append(("| " + StringUtil.join(resizedData[row], " | ")) + " |" + System.lineSeparator()
//					+ separator);
//		}
//		return sb.toString();
//	}
//
//	//	private static String lineSeparator(int columns, int size) {
//	//		StringBuilder sb = new StringBuilder();
//	//		String edge = StringUtil.repeat('-', size);
//	//		for (int i = 0; i < columns; i++) {
//	//			sb.append("+" + edge);
//	//		}
//	//		return sb.append("+").toString();
//	//
//	//	}
//
//}
