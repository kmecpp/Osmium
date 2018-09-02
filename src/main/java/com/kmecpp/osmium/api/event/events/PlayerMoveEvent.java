package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerMoveEvent extends PlayerEvent, Cancellable {

	Location getFrom();

	Location getTo();

}
