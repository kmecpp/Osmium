package com.kmecpp.osmium.api;

public interface Player {

	boolean respawn();

	void sendMessage(String message);

	default World getWorld() {
		return WorldManager.getWorld(this);
	}

}
