package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerJoinEvent;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.platform.bukkit.event.BukkitEvent;

public class BukkitPlayerJoinEvent extends BukkitEvent<org.bukkit.event.player.PlayerJoinEvent> implements PlayerJoinEvent {

	public BukkitPlayerJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
		super(event);
	}

	@Override
	public Player getPlayer() {
		return PlayerList.getPlayer(event.getPlayer().getName());
	}

}
