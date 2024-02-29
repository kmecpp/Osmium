package com.kmecpp.osmium.core;

import java.sql.Timestamp;
import java.util.Optional;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.api.DBColumn;
import com.kmecpp.osmium.api.database.api.DBTable;
import com.kmecpp.osmium.api.database.api.DatabaseType;
import com.kmecpp.osmium.api.database.api.Saveable;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;

public class NameHistory {

	public static void onLogin(PlayerConnectionEvent.Login e) {
		int userId = Osmium.getUserId(e.getUniqueId()).get();

		Optional<NameRecord> record = OsmiumCore.getDatabase()
				.select(NameRecord.class)
				.where("user_id=", userId)
				.orderByDesc("first_seen")
				.getFirst();

		if (record.isPresent() && record.get().name.equals(e.getPlayerName())) {
			record.get().updateLastSeen().save();
		} else {
			NameRecord.create(userId, e.getPlayerName()).save();
		}
	}

	@DBTable(name = "names", type = { DatabaseType.SQLITE, DatabaseType.MYSQL }, autoCreate = false)
	public static class NameRecord implements Saveable {

		@DBColumn(primary = true)
		private int userId;

		@DBColumn(primary = true, maxLength = 16)
		private String name;

		@DBColumn(primary = true)
		private Timestamp firstSeen;

		@DBColumn
		private Timestamp lastSeen;

		public static NameRecord create(int userId, String name) {
			long currentTime = System.currentTimeMillis();
			NameRecord record = new NameRecord();
			record.userId = userId;
			record.name = name;
			record.firstSeen = new Timestamp(currentTime);;
			record.lastSeen = new Timestamp(currentTime);
			return record;
		}

		public NameRecord updateLastSeen() {
			this.lastSeen = new Timestamp(System.currentTimeMillis());
			return this;
		}

	}

}
