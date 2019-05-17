package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;
import com.kmecpp.osmium.api.location.Location;

public interface PlayerMoveEvent extends PlayerEvent, Cancellable {

	default boolean isNewPosition() {
		return getFrom().getBlockX() != getTo().getBlockX() || getFrom().getBlockY() != getTo().getBlockY() || getFrom().getBlockZ() != getTo().getBlockZ();
	}

	default boolean isNewHorizontalPosition() {
		return getFrom().getBlockX() != getTo().getBlockX() || getFrom().getBlockZ() != getTo().getBlockZ();
	}

	Location getFrom();

	Location getTo();

	void setTo(Location location);

}
