package com.kmecpp.osmium.api.entity;

import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.cache.WorldList;

public interface Player extends EntityLiving, CommandSender {

	boolean respawn();

	default World getWorld() {
		return WorldList.getWorld(getWorldName());
	}

	String getWorldName();

	Inventory getInventory();

	GameMode getGameMode();

	void setGameMode(GameMode mode);

	Location getLocation();

	Direction getDirection();

	void teleport(Location location);

}
