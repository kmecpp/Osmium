package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.World;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.location.Location;

public class SpongePlayerMoveEvent implements PlayerMoveEvent {

	private MoveEntityEvent event;

	public SpongePlayerMoveEvent(MoveEntityEvent event) {
		this.event = event;
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getTargetEntity());
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

	@Override
	public void setTo(Location location) {
		event.setToTransform(new Transform<World>(location.<org.spongepowered.api.world.Location<World>> getSource()));
	}

	@Override
	public boolean shouldFire() {
		return event.getTargetEntity() instanceof org.spongepowered.api.entity.living.player.Player;
	}

}
