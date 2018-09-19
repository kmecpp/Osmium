package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerJoinEvent;

public class SpongePlayerJoinEvent implements PlayerJoinEvent {

	private ClientConnectionEvent.Join event;

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer(event.getTargetEntity());
	}

	@Override
	public ClientConnectionEvent.Join getSource() {
		return event;
	}

}
