package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerConnectionEvent {

	public interface Auth extends Event {
	}

	public interface Login extends Event {
	}

	public interface Join extends PlayerEvent {
	}

	public interface Quit extends PlayerEvent {
	}

}
