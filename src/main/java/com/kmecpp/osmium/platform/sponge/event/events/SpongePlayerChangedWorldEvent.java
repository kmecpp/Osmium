package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.entity.MoveEntityEvent;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.SpongeAccess;

public class SpongePlayerChangedWorldEvent implements com.kmecpp.osmium.api.event.events.PlayerChangedWorldEvent {

	private MoveEntityEvent.Teleport event;

	public SpongePlayerChangedWorldEvent(MoveEntityEvent.Teleport event) {
		this.event = event;
	}

	@Override
	public MoveEntityEvent.Teleport getSource() {
		return event;
	}

	@Override
	public World getFrom() {
		return WorldList.getWorld(event.getFromTransform().getExtent());
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getTargetEntity());
	}

	@Override
	public boolean shouldFire() {
		return event.getTargetEntity() instanceof org.spongepowered.api.entity.living.player.Player && !event.getFromTransform().getExtent().equals(event.getToTransform().getExtent());
	}

}
