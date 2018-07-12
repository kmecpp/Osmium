package com.kmecpp.osmium.api.tasks;

public enum TimeUnit {

	SECOND(20),
	MINUTE(20 * 60),
	HOUR(20 * 60 * 60),
	DAY(20 * 60 * 60 * 24);

	private long tickValue;

	private TimeUnit(long tickValue) {
		this.tickValue = tickValue;
	}

	public long getTickValue() {
		return tickValue;
	}

}
