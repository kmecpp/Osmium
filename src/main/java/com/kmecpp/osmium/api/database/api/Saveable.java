package com.kmecpp.osmium.api.database.api;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.MySQLDatabase;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.SQLiteDatabase;
import com.kmecpp.osmium.api.database.TableData;

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
		TableData table = SQLDatabase.getTable(this.getClass());
		//		if (this.getClass().isAnnotationPresent(MySQLTable.class)) {
		for (DatabaseType type : table.getTypes()) {
			if (type == DatabaseType.MYSQL) {
				MySQLDatabase database = Osmium.getPlugin(this.getClass()).getMySQLDatabase();
				if (flush || Osmium.isShuttingDown()) {
					database.replaceInto(this.getClass(), this);
				} else {
					database.replaceIntoAsync(this.getClass(), this);
				}
			} else if (type == DatabaseType.SQLITE) {
				SQLiteDatabase database = Osmium.getPlugin(this.getClass()).getSQLiteDatabase();
				if (flush || Osmium.isShuttingDown()) {
					database.replaceInto(this.getClass(), this);
				} else {
					database.replaceIntoAsync(this.getClass(), this);
				}
			}

		}
	}

}
