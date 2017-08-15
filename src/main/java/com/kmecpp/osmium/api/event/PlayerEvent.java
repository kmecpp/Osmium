package com.kmecpp.osmium.api.event;

import com.kmecpp.osmium.api.Player;

public class PlayerEvent extends Event {

	private final Player player;

	public PlayerEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

}
