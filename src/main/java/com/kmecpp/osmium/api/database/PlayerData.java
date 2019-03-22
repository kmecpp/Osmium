package com.kmecpp.osmium.api.database;

import java.util.UUID;

public abstract class PlayerData {

	@DBColumn(primary = true)
	protected UUID uuid;

	@DBColumn
	protected String name;

	public UUID getUniqueId() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void updatePlayerData(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

}
