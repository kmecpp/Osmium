package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerJoinEvent;
import com.kmecpp.osmium.cache.PlayerList;

public class BukkitPlayerJoinEvent implements PlayerJoinEvent {

	private org.bukkit.event.player.PlayerJoinEvent event;

	public BukkitPlayerJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
		this.event = event;
	}

	@Override
	public org.bukkit.event.player.PlayerJoinEvent getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return PlayerList.getPlayer(event.getPlayer().getName());
	}

}
