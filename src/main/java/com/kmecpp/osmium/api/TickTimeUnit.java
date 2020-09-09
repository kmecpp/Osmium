package com.kmecpp.osmium.api;

public enum TickTimeUnit {

	TICK(1),
	SECOND(20),
	MINUTE(20 * 60),
	HOUR(20 * 60 * 60),
	DAY(20 * 60 * 60 * 24);

	private int tickValue;

	private TickTimeUnit(int tickValue) {
		this.tickValue = tickValue;
	}

	public int getTickValue() {
		return tickValue;
	}

	public int getMillisecondValue() {
		return tickValue * 50;
	}

}
