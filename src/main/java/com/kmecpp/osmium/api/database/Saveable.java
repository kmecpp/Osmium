package com.kmecpp.osmium.api.database;

public interface Saveable {

	default void save(Database db) {
		save(db, false);
	}

	default void save(Database db, boolean flush) {
		if (flush) {
			db.replaceInto(this.getClass(), this);
		} else {
			db.replaceIntoAsync(this.getClass(), this);
		}
	}

}
