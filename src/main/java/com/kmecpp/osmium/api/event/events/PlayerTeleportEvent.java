package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;
import com.kmecpp.osmium.api.location.Location;

public interface PlayerTeleportEvent extends PlayerEvent, Cancellable {

	Location getTo();

	void setTo(Location location);

	Location getFrom();

	//	void setFrom(Location location);

}
