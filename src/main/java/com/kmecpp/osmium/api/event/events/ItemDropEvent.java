package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.EventAbstraction;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface ItemDropEvent extends EventAbstraction, Cancellable {

	public static interface Player extends ItemDropEvent, PlayerEvent {

	}

}
