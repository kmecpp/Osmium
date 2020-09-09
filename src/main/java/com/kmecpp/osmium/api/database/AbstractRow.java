package com.kmecpp.osmium.api.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AbstractRow {

	private ResultSet rs;

	public boolean getBoolean(int index) throws SQLException {
		return rs.getBoolean(index);
	}

	public boolean getBoolean(String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

	public byte getByte(int index) throws SQLException {
		return rs.getByte(index);
	}

	public byte getByte(String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	public short getShort(int index) throws SQLException {
		return rs.getShort(index);
	}

	public short getShort(String columnName) throws SQLException {
		return rs.getShort(columnName);
	}

	public int getInt(int index) throws SQLException {
		return rs.getInt(index);
	}

	public int getInt(String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	public long getLong(int index) throws SQLException {
		return rs.getLong(index);
	}

	public long getLong(String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	public float getFloat(int index) throws SQLException {
		return rs.getFloat(index);
	}

	public float getFloat(String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	public double getDouble(int index) throws SQLException {
		return rs.getDouble(index);
	}

	public double getDouble(String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	public String getString(int index) throws SQLException {
		return rs.getString(index);
	}

	public String getString(String columnName) throws SQLException {
		return rs.getString(columnName);
	}

}
