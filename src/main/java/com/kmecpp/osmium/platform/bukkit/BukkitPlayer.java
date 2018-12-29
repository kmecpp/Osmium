package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.cache.WorldList;

public class BukkitPlayer extends BukkitUser implements Player {

	private org.bukkit.entity.Player player;

	public BukkitPlayer(org.bukkit.entity.Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public org.bukkit.entity.Player getSource() {
		return player;
	}

	@Override
	public World getWorld() {
		return WorldList.getWorld(player.getWorld());
	}

	@Override
	public String getWorldName() {
		return player.getWorld().getName();
	}

	@Override
	public boolean respawn() {
		if (player.isDead()) {
			player.spigot().respawn();
			return true;
		}
		return false;
	}

	@Override
	public void sendRawMessage(String message) {
		player.sendMessage(message);
	}

	@Override
	public void setOp(boolean value) {
		player.setOp(value);
	}

	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public Inventory getInventory() {
		return new BukkitInventory(player.getInventory());
	}

	@Override
	public ItemStack getItemInMainHand() {
		return new BukkitItemStack(player.getInventory().getItemInMainHand());
	}

	@Override
	public ItemStack getItemInOffHand() {
		return new BukkitItemStack(player.getInventory().getItemInMainHand());
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.fromImplementation(player.getGameMode());
	}

	@Override
	public void setGameMode(GameMode mode) {
		player.setGameMode(mode.getSource());
	}

	@Override
	public double getHealth() {
		return getHealth();
	}

	@Override
	public void setHealth(double health) {
		player.setHealth(health);
	}

	@Override
	public Location getLocation() {
		return BukkitAccess.getLocation(player.getLocation());
	}

	@Override
	public Direction getDirection() {
		return new Direction(player.getLocation().getPitch(), player.getLocation().getYaw());
	}

	@Override
	public void teleport(Location location) {
		org.bukkit.Location l = (org.bukkit.Location) location.getImplementation();
		l.setDirection(player.getLocation().getDirection());
		player.teleport(l);
	}

	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}

	@Override
	public void setDisplayName(String name) {
		player.setDisplayName(name);
	}

}
