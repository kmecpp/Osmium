package com.kmecpp.osmium.platform.sponge.event;

import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerQuitEvent;

public class SpongePlayerQuitEvent implements PlayerQuitEvent {

	private ClientConnectionEvent.Disconnect event;

	public SpongePlayerQuitEvent(Disconnect event) {
		this.event = event;
	}

	@Override
	public ClientConnectionEvent.Disconnect getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer(event.getTargetEntity());
	}

}
