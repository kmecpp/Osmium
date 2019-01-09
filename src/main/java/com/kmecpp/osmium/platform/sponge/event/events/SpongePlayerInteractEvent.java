package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.action.InteractEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;

public class SpongePlayerInteractEvent implements PlayerInteractEvent {

	private InteractEvent event;

	public SpongePlayerInteractEvent(InteractEvent event) {
		this.event = event;
	}

	@Override
	public InteractEvent getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
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
	public boolean shouldFire() {
		return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
	}

}
