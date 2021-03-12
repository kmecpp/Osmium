package com.kmecpp.osmium.api.database;

public class Filter {

	private final FilterType type;
	private final String column;
	private final Object value;

	Filter(FilterType type, String column, Object value) {
		this.type = type;
		this.column = column;
		this.value = value;
	}

	public FilterType getType() {
		return type;
	}

	public String getColumn() {
		return column;
	}

	public Object getValue() {
		return value;
	}

	public String getSQL() {
		return column + type.sql + "?";
	}

	public static enum FilterType {

		EQ("="),
		NE("!="),
		LT("<"),
		GT(">"),
		LE("<="),
		GE(">="),

		;

		private String sql;

		private FilterType(String sql) {
			this.sql = sql;
		}

		public String getSQL() {
			return sql;
		}

	}

}
