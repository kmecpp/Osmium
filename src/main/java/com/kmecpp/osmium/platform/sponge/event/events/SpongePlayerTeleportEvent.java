package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.World;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerTeleportEvent;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.platform.SpongeAccess;

public class SpongePlayerTeleportEvent implements PlayerTeleportEvent {

	private MoveEntityEvent.Teleport event;

	public SpongePlayerTeleportEvent(MoveEntityEvent.Teleport event) {
		this.event = event;
	}

	@Override
	public MoveEntityEvent.Teleport getSource() {
		return event;
	}

	@Override
	public boolean shouldFire() {
		return event.getTargetEntity() instanceof org.spongepowered.api.entity.living.player.Player;
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getTargetEntity());
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
	public Location getTo() {
		return SpongeAccess.getLocation(event.getToTransform().getLocation());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setTo(Location location) {
		event.setToTransform(new Transform<World>((org.spongepowered.api.world.Location<World>) location.getSource()));
	}

	@Override
	public Location getFrom() {
		return SpongeAccess.getLocation(event.getFromTransform().getLocation());
	}

}
