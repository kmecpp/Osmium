package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.EventAbstraction;

public interface ServerListPingEvent extends EventAbstraction {

	String getDescription();

	void setDescription(String description);

	int getPlayersOnline();

	int getMaxPlayers();

	void setMaxPlayers(int maxPlayers);

}
