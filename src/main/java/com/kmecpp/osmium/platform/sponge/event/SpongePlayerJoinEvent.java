package com.kmecpp.osmium.platform.sponge.event;

import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerJoinEvent;
import com.kmecpp.osmium.platform.sponge.SpongePlayer;

public class SpongePlayerJoinEvent implements PlayerJoinEvent {

	private ClientConnectionEvent.Join event;

	public SpongePlayerJoinEvent(Join event) {
		this.event = event;
	}

	@Override
	public Player getPlayer() {
		return new SpongePlayer(event.getTargetEntity());
	}

	@Override
	public Object getSource() {
		return event;
	}

}
