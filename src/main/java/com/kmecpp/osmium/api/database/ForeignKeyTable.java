package com.kmecpp.osmium.api.database;

import com.kmecpp.osmium.api.database.api.DBColumn;

public abstract class ForeignKeyTable {

	@DBColumn(autoIncrement = true, primary = true)
	private int id;

	public int getId() {
		return id;
	}

}
