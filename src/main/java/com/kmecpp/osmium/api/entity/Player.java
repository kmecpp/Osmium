package com.kmecpp.osmium.api.entity;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.WorldManager;
import com.kmecpp.osmium.api.command.CommandSender;

public interface Player extends CommandSender {

	boolean respawn();

	default World getWorld() {
		return WorldManager.getWorld(this);
	}

}
