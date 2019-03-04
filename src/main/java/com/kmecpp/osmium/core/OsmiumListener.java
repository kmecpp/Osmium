package com.kmecpp.osmium.core;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;

public class OsmiumListener {

	@Listener
	public void on(PlayerConnectionEvent.Auth e) {
		Osmium.getPlayerDataManager().onPlayerAuthenticate(e);
	}

	@Listener
	public void on(PlayerConnectionEvent.Quit e) {
		Osmium.getPlayerDataManager().onPlayerQuit(e);
	}

}
