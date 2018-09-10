package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Event;

public interface ServerListPingEvent extends Event {

	String getDescription();

	void setDescription(String description);

	int getPlayersOnline();

	int getMaxPlayers();

	void setMaxPlayers(int maxPlayers);

}
