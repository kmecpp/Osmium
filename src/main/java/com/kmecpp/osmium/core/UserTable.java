package com.kmecpp.osmium.core;

import java.util.UUID;

import com.kmecpp.osmium.api.database.DBColumn;
import com.kmecpp.osmium.api.database.DBTable;
import com.kmecpp.osmium.api.database.mysql.MySQLTable;

@DBTable(name = "users")
@MySQLTable(name = "users", autoCreate = false)
public class UserTable {

	@DBColumn(primary = true, autoIncrement = true)
	private int id;

	@DBColumn(unique = true)
	private UUID uuid;

	@DBColumn(maxLength = 16)
	private String name;

	public int getId() {
		return id;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getName() {
		return name;
	}

}
