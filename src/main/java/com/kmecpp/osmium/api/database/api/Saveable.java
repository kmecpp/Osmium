package com.kmecpp.osmium.api.database.api;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

public interface Saveable {

	default void save() {
		save(false);
	}

	default void save(boolean flush) {
		DBTable table = this.getClass().getDeclaredAnnotation(DBTable.class);
		if (table == null) {
			OsmiumLogger.warn("Missing table registration for " + this.getClass().getName() + "! Is it annotated with @" + DBTable.class.getSimpleName() + "?");
			return;
		}

		for (DatabaseType type : table.type()) {
			SQLDatabase database = type == DatabaseType.MYSQL
					? Osmium.getPlugin(this.getClass()).getMySQLDatabase()
					: Osmium.getPlugin(this.getClass()).getSQLiteDatabase();

			if (flush || Osmium.isShuttingDown()) {
				database.replaceInto(this.getClass(), this);
			} else {
				database.replaceIntoAsync(this.getClass(), this);
			}
		}
	}

}
