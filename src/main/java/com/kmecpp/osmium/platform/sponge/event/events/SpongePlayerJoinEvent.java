package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.platform.sponge.event.SpongePlayerEvent;

public class SpongePlayerJoinEvent extends SpongePlayerEvent<ClientConnectionEvent.Join> {

	public SpongePlayerJoinEvent(Join event) {
		super(event);
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer(event.getTargetEntity());
	}

}
