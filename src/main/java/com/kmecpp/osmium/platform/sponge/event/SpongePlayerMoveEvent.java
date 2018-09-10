package com.kmecpp.osmium.platform.sponge.event;

import org.spongepowered.api.event.entity.MoveEntityEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;

public class SpongePlayerMoveEvent implements PlayerMoveEvent {

	private MoveEntityEvent event;

	@Override
	public Player getPlayer() {
		return (Player) event.getTargetEntity();
	}

	@Override
	public MoveEntityEvent getSource() {
		return event;
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(true);
	}

	@Override
	public Location getFrom() {
		return SpongeAccess.getLocation(event.getFromTransform().getLocation());
	}

	@Override
	public Location getTo() {
		return SpongeAccess.getLocation(event.getToTransform().getLocation());
	}

}
