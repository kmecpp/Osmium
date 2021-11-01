package com.kmecpp.osmium.api;

import java.util.UUID;

public class GameProfile {

	private final UUID uuid;
	private final String name;

	public GameProfile(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "(" + uuid + ", " + name + ")";
	}

}
