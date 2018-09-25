package com.kmecpp.osmium.platform.bukkit.event.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerChatEvent;

public class BukkitPlayerChatEvent implements PlayerChatEvent {

	private AsyncPlayerChatEvent event;

	public BukkitPlayerChatEvent(AsyncPlayerChatEvent event) {
		this.event = event;
	}

	@Override
	public AsyncPlayerChatEvent getSource() {
		return event;
	}

	@Override
	public String getMessage() {
		return event.getMessage();
	}

	@Override
	public void setMessage(String message) {
		event.setMessage(message);
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}

	@Override
	public Set<Player> getRecipients() {
		HashSet<Player> set = new HashSet<>();
		for (org.bukkit.entity.Player player : event.getRecipients()) {
			set.add(BukkitAccess.getPlayer(player));
		}
		return set;
	}

	@Override
	public Player getPlayer() {
		return BukkitAccess.getPlayer(event.getPlayer());
	}

}
