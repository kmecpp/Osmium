package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.event.Cancellable;

import com.kmecpp.osmium.api.event.EventAbstraction;

public interface EntityDamageEvent extends EventAbstraction, Cancellable {

	Entity getEntity();

}
