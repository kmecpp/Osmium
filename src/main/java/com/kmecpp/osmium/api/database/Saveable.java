package com.kmecpp.osmium.api.database;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.mysql.MySQLDatabase;
import com.kmecpp.osmium.api.database.mysql.MySQLTable;
import com.kmecpp.osmium.api.database.sqlite.Database;

public interface Saveable {

	default void save() {
		save(false);
	}

	default void save(boolean flush) {
		if (this.getClass().isAnnotationPresent(MySQLTable.class)) {
			MySQLDatabase database = Osmium.getPlugin(this.getClass()).getMySQLDatabase();
			if (flush) {
				database.replaceInto(this.getClass(), this);
			} else {
				database.replaceIntoAsync(this.getClass(), this);
			}
		} else {
			Database database = Osmium.getPlugin(this.getClass()).getSQLiteDatabase();
			if (flush) {
				database.replaceInto(this.getClass(), this);
			} else {
				database.replaceIntoAsync(this.getClass(), this);
			}
		}

	}

}
