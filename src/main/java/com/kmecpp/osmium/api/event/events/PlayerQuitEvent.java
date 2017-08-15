package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Player;
import com.kmecpp.osmium.api.event.PlayerEvent;

public class PlayerQuitEvent extends PlayerEvent {

	private String quitMessage;

	public PlayerQuitEvent(Player player, String quitMessage) {
		super(player);
	}

	public String getQuitMessage() {
		return quitMessage;
	}

	public void setQuitMessage(String quitMessage) {
		this.quitMessage = quitMessage;
	}

}
