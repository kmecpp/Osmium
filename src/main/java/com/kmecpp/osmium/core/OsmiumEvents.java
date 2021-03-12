package com.kmecpp.osmium.core;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.PlayerMoveEvent;
import com.kmecpp.osmium.api.plugin.SkipProcessing;
import com.kmecpp.osmium.platform.osmium.OsmiumPlayerMovePositionEvent;

@SkipProcessing
public class OsmiumEvents {

	@Listener
	public void on(PlayerMoveEvent e) {
		if (e.isNewPosition()) {
			Osmium.getEventManager().callEvent(new OsmiumPlayerMovePositionEvent(e));
		}
	}

}
