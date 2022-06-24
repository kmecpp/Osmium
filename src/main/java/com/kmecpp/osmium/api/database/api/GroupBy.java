package com.kmecpp.osmium.api.database.api;

public class GroupBy {
	
	private final String column;
	
	GroupBy(String column) {
		if (column == null) {
			throw new IllegalArgumentException("Database GroupBy column cannot be null!");
		} else if (column.isEmpty()) {
			throw new IllegalArgumentException("Database GroupBy column cannot be empty!");
		}
		
		this.column = column;
	}
	
	public static GroupBy of(String column) {
		return new GroupBy(column);
	}
	
	public String getColumn() {
		return column;
	}
	
	@Override
	public String toString() {
		return " GROUP BY " + column;
	}
	
}
