package com.kmecpp.osmium.api.tasks;

public enum TimeUnit {

	TICK(1),
	SECOND(20),
	MINUTE(20 * 60),
	HOUR(20 * 60 * 60),
	DAY(20 * 60 * 60 * 24);

	private int tickValue;

	private TimeUnit(int tickValue) {
		this.tickValue = tickValue;
	}

	public int getTickValue() {
		return tickValue;
	}

}
