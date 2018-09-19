package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerQuitEvent;
import com.kmecpp.osmium.cache.PlayerList;

public class BukkitPlayerQuitEvent implements PlayerQuitEvent {

	private org.bukkit.event.player.PlayerQuitEvent event;

	public BukkitPlayerQuitEvent(org.bukkit.event.player.PlayerQuitEvent event) {
		this.event = event;
	}

	@Override
	public org.bukkit.event.player.PlayerQuitEvent getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return PlayerList.getPlayer(event.getPlayer().getName());
	}

}
