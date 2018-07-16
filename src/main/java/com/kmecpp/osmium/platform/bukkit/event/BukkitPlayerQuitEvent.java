package com.kmecpp.osmium.platform.bukkit.event;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerQuitEvent;
import com.kmecpp.osmium.platform.bukkit.BukkitPlayer;

public class BukkitPlayerQuitEvent implements PlayerQuitEvent {

	private org.bukkit.event.player.PlayerQuitEvent event;

	@Override
	public org.bukkit.event.player.PlayerQuitEvent getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return new BukkitPlayer(event.getPlayer());
	}

}
