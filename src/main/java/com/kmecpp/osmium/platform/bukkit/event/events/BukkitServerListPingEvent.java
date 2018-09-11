package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.event.events.ServerListPingEvent;
import com.kmecpp.osmium.platform.bukkit.event.BukkitEvent;

public class BukkitServerListPingEvent extends BukkitEvent<org.bukkit.event.server.ServerListPingEvent> implements ServerListPingEvent {

	public BukkitServerListPingEvent(org.bukkit.event.server.ServerListPingEvent event) {
		super(event);
	}

	@Override
	public String getDescription() {
		return event.getMotd();
	}

	@Override
	public void setDescription(String description) {
		event.setMotd(description);
	}

	@Override
	public int getPlayersOnline() {
		return event.getNumPlayers();
	}

	@Override
	public int getMaxPlayers() {
		return event.getMaxPlayers();
	}

	@Override
	public void setMaxPlayers(int maxPlayers) {
		event.setMaxPlayers(maxPlayers);
	}

}
