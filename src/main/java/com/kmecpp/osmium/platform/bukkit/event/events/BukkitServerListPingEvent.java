package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.event.events.ServerListPingEvent;

public class BukkitServerListPingEvent implements ServerListPingEvent {

	private org.bukkit.event.server.ServerListPingEvent event;

	public BukkitServerListPingEvent(org.bukkit.event.server.ServerListPingEvent event) {
		this.event = event;
	}

	@Override
	public org.bukkit.event.server.ServerListPingEvent getSource() {
		return event;
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
