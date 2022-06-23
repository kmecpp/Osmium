package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.event.events.EntityDamageEvent;
import com.kmecpp.osmium.platform.BukkitAccess;

public class BukkitEntityDamageEvent implements EntityDamageEvent {

	private org.bukkit.event.entity.EntityDamageEvent event;

	public BukkitEntityDamageEvent(org.bukkit.event.entity.EntityDamageEvent event) {
		this.event = event;
	}

	@Override
	public org.bukkit.event.entity.EntityDamageEvent getSource() {
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
		return BukkitAccess.getEntity(event.getEntity());
	}

}
