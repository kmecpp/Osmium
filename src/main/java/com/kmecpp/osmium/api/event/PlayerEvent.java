package com.kmecpp.osmium.api.event;

import com.kmecpp.osmium.api.entity.Player;

public interface PlayerEvent extends Event {

	Player getPlayer();

}
