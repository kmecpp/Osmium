package com.kmecpp.osmium.api.event;

public interface Event {

	default boolean shouldFire() {
		return true;
	}

}
