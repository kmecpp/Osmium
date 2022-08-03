package com.kmecpp.osmium.api.database.api;

public class JoinClause {

	private final String rightTable;
	private final JoinType joinType;
	private final String criteria;

	public JoinClause(JoinType joinType, String rightTable, String criteria) {
		this.rightTable = rightTable;
		this.joinType = joinType;
		this.criteria = criteria;
	}

	public String getRightTable() {
		return rightTable;
	}

	public JoinType getType() {
		return joinType;
	}

	public String getCriteria() {
		return criteria;
	}

	@Override
	public String toString() {
		return " " + joinType + " JOIN " + rightTable + (criteria != null ? " ON " + criteria : "");
	}

	public static enum JoinType {

		LEFT,
		RIGHT,
		INNER,
		CROSS,

		;

	}

}
