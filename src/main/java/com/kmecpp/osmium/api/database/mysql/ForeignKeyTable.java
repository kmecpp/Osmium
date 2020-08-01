package com.kmecpp.osmium.api.database.mysql;

import com.kmecpp.osmium.api.database.DBColumn;

public abstract class ForeignKeyTable {

	@DBColumn(autoIncrement = true, primary = true)
	private int id;

	public int getId() {
		return id;
	}

}
