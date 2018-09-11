package com.kmecpp.osmium.platform.bukkit.event;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Location;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;

public class BukkitPlayerMoveEvent implements PlayerMoveEvent {

	private org.bukkit.event.player.PlayerMoveEvent e;

	@Override
	public Player getPlayer() {
		return Osmium.getPlayer(e.getPlayer().getName()).get();
	}

	@Override
	public Object getSource() {
		return e;
	}

	@Override
	public boolean isCancelled() {
		return e.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		e.setCancelled(cancel);
	}

	@Override
	public Location getFrom() {
		return BukkitAccess.getLocation(e.getFrom());
	}

	@Override
	public Location getTo() {
		return BukkitAccess.getLocation(e.getTo());
	}

}
