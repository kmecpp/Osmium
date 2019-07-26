package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerChangedWorldEvent extends PlayerEvent {

	World getFrom();

}
