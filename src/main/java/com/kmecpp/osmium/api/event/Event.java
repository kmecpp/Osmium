package com.kmecpp.osmium.api.event;

import com.kmecpp.osmium.api.Abstraction;

public interface Event extends Abstraction {

	default boolean shouldFire() {
		return true;
	}

}
