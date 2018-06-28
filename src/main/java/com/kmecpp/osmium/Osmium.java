package com.kmecpp.osmium;

import com.kmecpp.osmium.api.event.EventManager;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.tasks.Scheduler;

public final class Osmium {

	public static final String OSMIUM = "Osmium";

	private static EventManager eventManager = new EventManager();
	private static Scheduler scheduler = new Scheduler();

	private Osmium() {
	}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

}
