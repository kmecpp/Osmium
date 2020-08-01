package com.kmecpp.osmium.api.database;

import java.util.UUID;

public abstract class MultiplePlayerData<T> implements Saveable {

	@DBColumn(primary = true)
	protected UUID uuid;

	@DBColumn(maxLength = 16)
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

	public abstract T getKey();

	//	public abstract T process(ResultSet rs) throws SQLException;

}
