package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerQuitEvent;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.platform.bukkit.event.BukkitEvent;

public class BukkitPlayerQuitEvent extends BukkitEvent<org.bukkit.event.player.PlayerQuitEvent> implements PlayerQuitEvent {

	public BukkitPlayerQuitEvent(org.bukkit.event.player.PlayerQuitEvent event) {
		super(event);
	}

	@Override
	public Player getPlayer() {
		return PlayerList.getPlayer(event.getPlayer().getName());
	}

}
