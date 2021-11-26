package com.kmecpp.osmium.api.database;

import java.util.UUID;

import com.kmecpp.osmium.api.database.api.DBColumn;
import com.kmecpp.osmium.api.database.api.Saveable;
import com.kmecpp.osmium.api.util.StringUtil;

public abstract class PlayerData implements Saveable {

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
		onLoad();
	}

	public void onLoad() {
	}

	@Override
	public String toString() {
		return StringUtil.toString(this);
	}

}
