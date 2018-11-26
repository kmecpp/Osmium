package com.kmecpp.osmium.api;

import java.util.UUID;

import com.kmecpp.osmium.Osmium;

public class User {

	private UUID uniqueId;

	private String name;

	private long lastPlayed;

	public User(UUID uniqueId, String name, long lastPlayed) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.lastPlayed = lastPlayed;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public String getName() {
		return name;
	}

	public long getLastPlayed() {
		return lastPlayed;
	}

	public boolean isOnline() {
		return Osmium.getPlayer(name) != null;
	}

	//	UUID getUniqueId();
	//
	//	String getLastKnownName();

}
