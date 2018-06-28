package com.kmecpp.osmium.api.event;

import com.kmecpp.osmium.api.Player;

public interface PlayerEvent extends Event {

	Player getPlayer();

}
