package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.osmium.DateChangeEvent;

public class OsmiumWeekChangeEvent implements DateChangeEvent.Week {

	private int previous;
	private int current;

	public OsmiumWeekChangeEvent(int previous, int current) {
		this.previous = previous;
		this.current = current;
	}

	/**
	 * @return the current week of the month
	 */
	@Override
	public int getCurrent() {
		return current;
	}

	/**
	 * @return the previous week of the month
	 */
	@Override
	public int getPrevious() {
		return previous;
	}

}
