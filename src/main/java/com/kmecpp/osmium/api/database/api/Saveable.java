package com.kmecpp.osmium.api.database.api;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.TableData;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

public interface Saveable {

	default void save() {
		save(false);
	}

	default void save(boolean flush) {
		TableData table = SQLDatabase.getTable(this.getClass());
		if (table == null) {
			OsmiumLogger.warn("Classes implementing Savable must be annotated with @" + DBTable.class.getSimpleName());
			return;
		}

		for (DatabaseType type : table.getTypes()) {
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
