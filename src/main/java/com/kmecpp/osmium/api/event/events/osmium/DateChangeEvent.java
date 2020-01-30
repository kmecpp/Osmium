package com.kmecpp.osmium.api.event.events.osmium;

import com.kmecpp.osmium.api.event.Event;

public interface DateChangeEvent extends Event {

	int getCurrent();

	int getPrevious();

	public interface Day extends DateChangeEvent {
	}

	public interface Week extends DateChangeEvent {
	}

	public interface Month extends DateChangeEvent {
	}

}
