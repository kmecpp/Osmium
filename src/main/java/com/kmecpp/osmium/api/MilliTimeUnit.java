package com.kmecpp.osmium.api;

public enum MilliTimeUnit {

	MILLISECOND(1),
	SECOND(1000),
	MINUTE(1000 * 60),
	HOUR(1000 * 60 * 60),
	DAY(1000 * 60 * 60 * 24);

	private long millisecondTime;

	private MilliTimeUnit(long millisecondTime) {
		this.millisecondTime = millisecondTime;
	}

	public long getMillisecondTime() {
		return millisecondTime;
	}

}
