package com.kmecpp.osmium.api.database;

public interface AsyncSave {

	default void save(Database db) {
		db.replaceInto(this.getClass(), this);
	}

}
