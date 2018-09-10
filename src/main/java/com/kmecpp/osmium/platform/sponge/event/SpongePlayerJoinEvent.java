package com.kmecpp.osmium.platform.sponge.event;

import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerJoinEvent;

public class SpongePlayerJoinEvent implements PlayerJoinEvent {

	private ClientConnectionEvent.Join event;

	public SpongePlayerJoinEvent(Join event) {
		this.event = event;
	}

	@Override
	public ClientConnectionEvent.Join getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer(event.getTargetEntity());
	}

}
