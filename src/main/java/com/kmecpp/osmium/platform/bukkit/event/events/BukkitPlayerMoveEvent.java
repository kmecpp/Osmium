package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;

public class BukkitPlayerMoveEvent implements PlayerMoveEvent {

	private org.bukkit.event.player.PlayerMoveEvent event;

	public BukkitPlayerMoveEvent(org.bukkit.event.player.PlayerMoveEvent event) {
		this.event = event;
	}

	@Override
	public org.bukkit.event.player.PlayerMoveEvent getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return BukkitAccess.getPlayer(event.getPlayer());
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
	public Location getFrom() {
		return BukkitAccess.getLocation(event.getFrom());
	}

	@Override
	public Location getTo() {
		return BukkitAccess.getLocation(event.getTo());
	}

}
