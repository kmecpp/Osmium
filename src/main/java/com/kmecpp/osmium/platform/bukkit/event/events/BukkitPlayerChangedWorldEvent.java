package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.BukkitAccess;

public class BukkitPlayerChangedWorldEvent implements com.kmecpp.osmium.api.event.events.PlayerChangedWorldEvent {

	private PlayerChangedWorldEvent event;

	public BukkitPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
		this.event = event;
	}

	@Override
	public PlayerChangedWorldEvent getSource() {
		return event;
	}

	@Override
	public World getFrom() {
		return WorldList.getWorld(event.getFrom());
	}

	@Override
	public Player getPlayer() {
		return BukkitAccess.getPlayer(event.getPlayer());
	}

}
