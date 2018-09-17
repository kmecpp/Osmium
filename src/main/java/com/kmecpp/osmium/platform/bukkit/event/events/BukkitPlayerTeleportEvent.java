package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerTeleportEvent;
import com.kmecpp.osmium.platform.bukkit.event.BukkitEvent;

public class BukkitPlayerTeleportEvent extends BukkitEvent<org.bukkit.event.player.PlayerTeleportEvent> implements PlayerTeleportEvent {

	public BukkitPlayerTeleportEvent(org.bukkit.event.player.PlayerTeleportEvent event) {
		super(event);
	}

	@Override
	public Location getFrom() {
		return BukkitAccess.getLocation(event.getFrom());
	}

	//	@Override
	//	public void setFrom(Location location) {
	//		event.setFrom(location.getImplementation());
	//	}

	@Override
	public Location getTo() {
		return BukkitAccess.getLocation(event.getTo());
	}

	@Override
	public void setTo(Location location) {
		event.setTo(location.getImplementation());
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
	public Player getPlayer() {
		return BukkitAccess.getPlayer(event.getPlayer());
	}

}
