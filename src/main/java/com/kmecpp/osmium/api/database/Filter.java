package com.kmecpp.osmium.api.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.kmecpp.osmium.api.database.mysql.MDBUtil;

public class Filter {

	private ArrayList<String> filters = new ArrayList<>(); //Ex: column>=
	private ArrayList<Object> values = new ArrayList<>();

	Filter(String filter, Object value) {
		if (filter == null) {
			throw new IllegalArgumentException("Database filter cannot be null!");
		} else if (filter.isEmpty()) {
			throw new IllegalArgumentException("Database filter cannot be empty!");
		}
		filter = filter.trim();
		char lastChar = filter.charAt(filter.length() - 1);
		if (lastChar != '=' && lastChar != '>' && lastChar != '<') {
			throw new IllegalArgumentException("Invalid database filter: '" + filter + "'");
		}

		this.filters.add(filter);
		this.values.add(value);
	}

	public static Filter of(String filter, Object value) {
		return new Filter(filter, value);
	}

	public Filter and(String filter, Object value) {
		this.filters.add(filter);
		return this;
	}

	public int size() {
		return filters.size();
	}

	public ArrayList<String> getFilters() {
		return filters;
	}

	public Object getValue(int filterIndex) {
		return values.get(filterIndex);
	}

	public void link(PreparedStatement ps) throws SQLException {
		for (int i = 0; i < filters.size(); i++) {
			MDBUtil.updatePreparedStatement(ps, i + 1, values.get(i));
		}
	}

}
