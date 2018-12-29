package com.kmecpp.osmium.api.entity;

import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;

public interface Player extends User, EntityLiving, CommandSender {

	boolean respawn();

	World getWorld();

	String getWorldName();

	Inventory getInventory();

	ItemStack getItemInMainHand();

	ItemStack getItemInOffHand();

	GameMode getGameMode();

	void setGameMode(GameMode mode);

	Location getLocation();

	Direction getDirection();

	void teleport(Location location);

}
