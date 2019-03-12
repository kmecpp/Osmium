package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.DateChangeEvent;

public class OsmiumMonthChangeEvent implements DateChangeEvent.Month {

	private int previous;
	private int current;

	public OsmiumMonthChangeEvent(int previous, int current) {
		this.previous = previous;
		this.current = current;
	}

	/**
	 * @return the current month of the year
	 */
	@Override
	public int getCurrent() {
		return current;
	}

	/**
	 * @return the previous month
	 */
	@Override
	public int getPrevious() {
		return previous;
	}

}
