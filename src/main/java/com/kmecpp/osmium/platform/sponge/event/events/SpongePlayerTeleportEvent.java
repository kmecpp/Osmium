package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent.Teleport;
import org.spongepowered.api.world.World;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.event.events.PlayerTeleportEvent;
import com.kmecpp.osmium.platform.sponge.event.SpongePlayerEvent;

public class SpongePlayerTeleportEvent extends SpongePlayerEvent<MoveEntityEvent.Teleport> implements PlayerTeleportEvent {

	public SpongePlayerTeleportEvent(Teleport event) {
		super(event);
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
		event.setToTransform(new Transform<World>((org.spongepowered.api.world.Location<World>) location.getImplementation()));
	}

	@Override
	public Location getFrom() {
		return SpongeAccess.getLocation(event.getFromTransform().getLocation());
	}

}
