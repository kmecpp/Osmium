package com.kmecpp.osmium.api.database.api;

public class LimitClause {

	private final int offset;
	private final int rowCount;

	public LimitClause(int offset, int rowCount) {
		this.offset = offset;
		this.rowCount = rowCount;
	}

	public int getOffset() {
		return offset;
	}

	public int getRowCount() {
		return rowCount;
	}

	@Override
	public String toString() {
		return offset == 0
				? " LIMIT " + rowCount
				: " LIMIT " + offset + ", " + rowCount;
	}

}
