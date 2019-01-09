package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerInteractEvent extends PlayerEvent, Cancellable {

	public interface Item extends PlayerInteractEvent {

	}

	public interface Block extends PlayerInteractEvent {

	}

	public interface Entity extends PlayerInteractEvent {

	}

	public interface Physical extends PlayerInteractEvent {

	}

}
