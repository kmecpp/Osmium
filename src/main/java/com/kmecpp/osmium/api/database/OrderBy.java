package com.kmecpp.osmium.api.database;

public class OrderBy {

	private static final String ASC = "ASC";
	private static final String DESC = "DESC";

	private String direction;
	private String column;

	private OrderBy(String direction, String column) {
		this.direction = direction;
		this.column = column;
	}

	public static OrderBy asc(String column) {
		return new OrderBy(ASC, column);
	}

	public static OrderBy desc(String column) {
		return new OrderBy(DESC, column);
	}

	public String getDirection() {
		return direction;
	}

	public String getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return "ORDER BY " + column + " " + direction;
	}

}
