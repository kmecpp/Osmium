package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerConnectionEvent extends Event {

	String getPlayerName();

	public interface Auth extends PlayerConnectionEvent {
	}

	public interface Login extends PlayerConnectionEvent {
	}

	public interface Join extends PlayerConnectionEvent, PlayerEvent {
	}

	public interface Quit extends PlayerConnectionEvent, PlayerEvent {
	}

}
