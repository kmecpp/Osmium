package com.kmecpp.osmium.api.event.events;

public interface PlayerMovePositionEvent extends PlayerMoveEvent {

	@Override
	default boolean isNewPosition() {
		return true;
	}

}
