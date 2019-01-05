package com.kmecpp.osmium.api.entity;

import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;

public interface Player extends User, EntityLiving, CommandSender {

	boolean respawn();

	Inventory getInventory();

	ItemStack getItemInMainHand();

	ItemStack getItemInOffHand();

	GameMode getGameMode();

	void setGameMode(GameMode mode);

}
