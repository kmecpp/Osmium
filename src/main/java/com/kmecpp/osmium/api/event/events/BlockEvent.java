package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.event.EventAbstraction;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface BlockEvent extends EventAbstraction {

	Block getBlock();

	public interface Break extends BlockEvent, PlayerEvent {
	}

	public interface Place extends BlockEvent, PlayerEvent {
	}

	public interface PlayerChange extends Break, Place {
	}

	public interface Explode extends BlockEvent {
	}

	public interface Grow extends BlockEvent {
	}

	public interface Fade extends BlockEvent {
	}

}
