package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerTeleportEvent extends PlayerEvent, Cancellable {

	Location getTo();

	void setTo(Location location);

	Location getFrom();

	//	void setFrom(Location location);

}
