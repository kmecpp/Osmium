package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.entity.DamageEntityEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.event.events.EntityDamageEvent;

public class SpongeEntityDamageEvent implements EntityDamageEvent {

	private DamageEntityEvent event;

	public SpongeEntityDamageEvent(DamageEntityEvent event) {
		this.event = event;
	}

	@Override
	public DamageEntityEvent getSource() {
		return event;
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
	public Entity getEntity() {
		return SpongeAccess.getEntity(event.getTargetEntity());
	}

}
