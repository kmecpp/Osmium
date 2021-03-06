package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.event.EventAbstraction;

public interface EntityEvent extends EventAbstraction {

	Entity getEntity();

	EntityType getEntityType();

	interface Target extends EntityEvent {
	}

	interface Spawn extends EntityEvent {
	}

	interface Damage extends EntityEvent {
	}

}
