package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Player;
import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;

public class PlayerChatEvent extends PlayerEvent implements Cancellable {

	private final String originalMessage;
	private String message;

	private boolean cancelled;

	public PlayerChatEvent(Player player, String message) {
		super(player);
		this.originalMessage = message;
		this.message = message;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public String getOriginalMessage() {
		return originalMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
