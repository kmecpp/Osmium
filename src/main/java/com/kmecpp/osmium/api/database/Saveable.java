package com.kmecpp.osmium.api.database;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.mysql.MDBTableData;
import com.kmecpp.osmium.api.database.mysql.MySQLDatabase;
import com.kmecpp.osmium.api.database.sqlite.SQLiteDatabase;

public interface Saveable {

	default void save() {
		save(false);
	}

	default void save(boolean flush) {
		//		DBTable table = this.getClass().getAnnotation(DBTable.class);
		//		if (table == null) {
		//			OsmiumLogger.warn("Classes implementing Savable must be annotated with @" + DBTable.class.getSimpleName());
		//			return;
		//		}
		MDBTableData table = SQLDatabase.getTable(this.getClass());
		//		if (this.getClass().isAnnotationPresent(MySQLTable.class)) {
		for (DatabaseType type : table.getTypes()) {
			if (type == DatabaseType.MYSQL) {
				MySQLDatabase database = Osmium.getPlugin(this.getClass()).getMySQLDatabase();
				if (flush) {
					database.replaceInto(this.getClass(), this);
				} else {
					database.replaceIntoAsync(this.getClass(), this);
				}
			} else if (type == DatabaseType.SQLITE) {
				SQLiteDatabase database = Osmium.getPlugin(this.getClass()).getSQLiteDatabase();
				if (flush) {
					database.replaceInto(this.getClass(), this);
				} else {
					database.replaceIntoAsync(this.getClass(), this);
				}
			}

		}
	}

}
