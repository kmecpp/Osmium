package com.kmecpp.osmium.api.database.api;

public class OrderByClause {

	private final int offset;
	private final int rowCount;

	public OrderByClause(int offset, int rowCount) {
		this.offset = offset;
		this.rowCount = rowCount;
	}

	public int getOffset() {
		return offset;
	}

	public int getRowCount() {
		return rowCount;
	}

}
