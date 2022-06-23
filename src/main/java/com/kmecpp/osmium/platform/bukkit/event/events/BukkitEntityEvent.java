package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.entity.EntitySpawnEvent;

import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.platform.BukkitAccess;

public abstract class BukkitEntityEvent implements EntityEvent {

	private org.bukkit.event.entity.EntityEvent event;

	@Override
	public Entity getEntity() {
		return BukkitAccess.getEntity(event.getEntity());
	}

	@Override
	public EntityType getEntityType() {
		return getEntity().getType();
	}

	public BukkitEntityEvent(org.bukkit.event.entity.EntityEvent event) {
		this.event = event;
	}

	public static class BukkitEntitySpawnEvent extends BukkitEntityEvent {

		private EntitySpawnEvent event;

		public BukkitEntitySpawnEvent(org.bukkit.event.entity.EntityEvent event) {
			super(event);
		}

		@Override
		public EntitySpawnEvent getSource() {
			return event;
		}

	}

}
