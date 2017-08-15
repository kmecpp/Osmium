package com.kmecpp.osmium;

import com.kmecpp.osmium.api.event.EventManager;

public final class Osmium {

	public static final String OSMIUM = "Osmium";

	private static EventManager eventManager = new EventManager();

	private Osmium() {
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

}
