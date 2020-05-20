package com.kmecpp.osmium.api.database;

import com.kmecpp.osmium.Osmium;

public interface Saveable {

	default void save() {
		save(false);
	}

	default void save(boolean flush) {
		Database database = Osmium.getPlugin(this.getClass()).getDatabase();
		if (flush) {
			database.replaceInto(this.getClass(), this);
		} else {
			database.replaceIntoAsync(this.getClass(), this);
		}
	}

}
