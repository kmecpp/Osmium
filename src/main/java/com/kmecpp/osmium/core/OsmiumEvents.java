package com.kmecpp.osmium.core;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.platform.osmium.OsmiumPlayerMovePositionEvent;

public class OsmiumEvents {

	@Listener
	public void on(PlayerMoveEvent e) {
		System.out.println("Player move!");
		if (e.isNewPosition()) {
			Osmium.getEventManager().callEvent(new OsmiumPlayerMovePositionEvent(e));
		}
	}

}
