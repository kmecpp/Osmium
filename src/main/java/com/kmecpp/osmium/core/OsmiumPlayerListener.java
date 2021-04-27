package com.kmecpp.osmium.core;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.Order;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;

public class OsmiumPlayerListener {

	@Listener(order = Order.FIRST)
	public void on(PlayerConnectionEvent.Auth e) {
		OsmiumUserIds.onAsyncPreLogin(e.getUniqueId(), e.getPlayerName());
	}

	@Listener(order = Order.FIRST)
	public void on(PlayerConnectionEvent.Login e) {
	}

	@Listener(order = Order.LAST)
	public void on(PlayerConnectionEvent.Quit e) {
		Osmium.getPlayerDataManager().onPlayerQuit(e);
	}

}