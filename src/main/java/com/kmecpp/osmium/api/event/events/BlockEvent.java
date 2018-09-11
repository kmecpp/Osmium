package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface BlockEvent {

	Block getBlock();

	public interface Break extends BlockEvent, PlayerEvent {
	}

	public interface Place extends BlockEvent, PlayerEvent {
	}

	public interface Modify extends BlockEvent, PlayerEvent {
	}

	public interface Explode extends BlockEvent {
	}

	public interface Grow extends BlockEvent {
	}

	public interface Fade extends BlockEvent {
	}

}
