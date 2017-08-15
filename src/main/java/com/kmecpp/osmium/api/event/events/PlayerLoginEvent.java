package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Player;
import com.kmecpp.osmium.api.event.PlayerEvent;

public class PlayerLoginEvent extends PlayerEvent {

	public PlayerLoginEvent(Player player) {
		super(player);
	}

}
