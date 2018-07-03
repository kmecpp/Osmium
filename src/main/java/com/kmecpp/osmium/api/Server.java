package com.kmecpp.osmium.api;

import java.util.Collection;

import com.kmecpp.osmium.api.entity.Player;

public interface Server extends Abstraction {

	Collection<Player> getOnlinePlayers();

}
