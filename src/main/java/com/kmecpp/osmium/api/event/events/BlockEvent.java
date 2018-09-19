package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface BlockEvent extends Event {

	Block getBlock();

	public interface Break extends BlockEvent, PlayerEvent {
	}

	public interface Place extends BlockEvent, PlayerEvent {
	}

	public interface Modify extends BlockEvent {
	}

	public interface Explode extends BlockEvent {
	}

	public interface Grow extends BlockEvent {
	}

	public interface Fade extends BlockEvent {
	}

}
