package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.server.ClientPingServerEvent;

import com.kmecpp.osmium.api.event.events.ServerListPingEvent;
import com.kmecpp.osmium.platform.SpongeAccess;

public class SpongeServerListPingEvent implements ServerListPingEvent {

	private ClientPingServerEvent event;

	public SpongeServerListPingEvent(ClientPingServerEvent event) {
		this.event = event;
	}

	@Override
	public String getDescription() {
		return event.getResponse().getDescription().toString();
	}

	@Override
	public void setDescription(String description) {
		event.getResponse().setDescription(SpongeAccess.getText(description));
	}

	@Override
	public int getPlayersOnline() {
		return event.getResponse().getPlayers().get().getOnline();
	}

	@Override
	public int getMaxPlayers() {
		return event.getResponse().getPlayers().get().getMax();
	}

	@Override
	public void setMaxPlayers(int maxPlayers) {
		event.getResponse().getPlayers().get().setMax(maxPlayers);
	}

	@Override
	public ClientPingServerEvent getSource() {
		return event;
	}

}
