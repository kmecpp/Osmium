package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.osmium.DateChangeEvent;

public class OsmiumDayChangeEvent implements DateChangeEvent.Day {

	private final int previous;
	private final int current;

	public OsmiumDayChangeEvent(int previous, int current) {
		this.previous = previous;
		this.current = current;
	}

	/**
	 * @return the current day
	 */
	@Override
	public int getCurrent() {
		return current;
	}

	/**
	 * @return the day month
	 */
	@Override
	public int getPrevious() {
		return previous;
	}

}
