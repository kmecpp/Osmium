package com.kmecpp.osmium.api;

public interface Player extends Abstraction {

	String getName();

	boolean respawn();

	void sendMessage(String message);

	default World getWorld() {
		return WorldManager.getWorld(this);
	}

}
