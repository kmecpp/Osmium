package com.kmecpp.osmium.core;

import java.time.ZoneId;
import java.util.UUID;

import com.kmecpp.osmium.api.database.api.DBColumn;
import com.kmecpp.osmium.api.database.api.DBTable;
import com.kmecpp.osmium.api.database.api.DatabaseType;

@DBTable(name = "users", type = { DatabaseType.SQLITE, DatabaseType.MYSQL }, autoCreate = false)
//@MySQLTable(name = "users", autoCreate = false)
public class UserTable {

	@DBColumn(primary = true, autoIncrement = true)
	private int id;

	@DBColumn(unique = true)
	private UUID uuid;

	@DBColumn(maxLength = 16)
	private String name;

	@DBColumn(maxLength = 64, nullable = true)
	private String timeZone;

	public static UserTable createFakeUserTable() {
		UserTable result = new UserTable();
		result.uuid = new UUID(0, 0);
		result.name = "[Fake User]";
		result.id = -1;
		return result;
	}

	public int getId() {
		return id;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public ZoneId getTimeZone() {
		return timeZone != null ? ZoneId.of(timeZone) : null;
	}

	public void setTimezone(ZoneId timeZone) {
		this.timeZone = timeZone != null ? timeZone.getId() : null;
	}

}
