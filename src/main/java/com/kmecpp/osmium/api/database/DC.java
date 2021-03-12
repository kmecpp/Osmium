package com.kmecpp.osmium.api.database;

import com.kmecpp.osmium.api.database.Filter.FilterType;

public class DC {

	public static DCBuilder of(String column) {
		return new DCBuilder(column);
	}

	public static class DCBuilder {

		private String column;

		public DCBuilder(String column) {
			this.column = column;
		}

		public Filter equalTo(Object value) {
			return new Filter(FilterType.EQ, column, value);
		}

		public Filter notEqualTo(Object value) {
			return new Filter(FilterType.NE, column, value);
		}

		public Filter lessThan(Object value) {
			return new Filter(FilterType.LT, column, value);
		}

		public Filter greaterThan(Object value) {
			return new Filter(FilterType.GT, column, value);
		}

		public Filter lessThanEqualTo(Object value) {
			return new Filter(FilterType.LE, column, value);
		}

		public Filter greaterThanEqualTo(Object value) {
			return new Filter(FilterType.GE, column, value);
		}

	}

}
