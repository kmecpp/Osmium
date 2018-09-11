package com.kmecpp.osmium.platform.sponge.event;

import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.PlayerEvent;

public class SpongePlayerEvent<T extends TargetPlayerEvent> extends SpongeEvent<T> implements PlayerEvent {

	public SpongePlayerEvent(T event) {
		super(event);
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getTargetEntity());
	}

	@Override
	public boolean shouldFire() {
		return event.getTargetEntity() instanceof org.spongepowered.api.entity.living.player.Player;
	}

}
