package com.kmecpp.osmium.api.database;

import java.util.UUID;

public abstract class PlayerData {

	@DBColumn(primary = true)
	private UUID uuid;

	@DBColumn
	private String name;

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
