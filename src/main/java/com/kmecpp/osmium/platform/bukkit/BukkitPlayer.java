package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.util.Vector;

import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.location.Vector3d;

public class BukkitPlayer extends BukkitEntityLiving implements Player {

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
	public boolean respawn() {
		if (player.isDead()) {
			player.spigot().respawn();
			return true;
		}
		return false;
	}

	@Override
	public void sendMessage(String message) {
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

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemInMainHand() {
		try {
			return new BukkitItemStack(player.getInventory().getItemInMainHand());
		} catch (NoSuchMethodError e) {
			return new BukkitItemStack(player.getInventory().getItemInHand());
		}
	}

	@Override
	public ItemStack getItemInOffHand() {
		return new BukkitItemStack(player.getInventory().getItemInMainHand());
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.fromSource(player.getGameMode());
	}

	@Override
	public void setGameMode(GameMode mode) {
		player.setGameMode(mode.getSource());
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public boolean isOp() {
		return player.isOp();
	}

	@Override
	public long getLastPlayed() {
		return player.getLastPlayed();
	}

	@Override
	public long getFirstPlayed() {
		return player.getFirstPlayed();
	}

	@Override
	public boolean hasPlayedBefore() {
		return player.hasPlayedBefore();
	}

	@Override
	public boolean isOnline() {
		return player.isOnline();
	}

	@Override
	public void openInventory(Inventory inventory) {
		player.openInventory((org.bukkit.inventory.Inventory) inventory.getSource());
	}

	@Override
	public void closeInventory() {
		player.closeInventory();
	}

	@Override
	public Vector3d getVelocity() {
		Vector v = player.getVelocity();
		return new Vector3d(v.getX(), v.getY(), v.getZ());
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		player.setVelocity(new Vector(x, y, z));
	}

	@Override
	public void kick() {
		player.kickPlayer("You have been kicked from the server!");
	}

	@Override
	public void kick(String message) {
		player.kickPlayer(message);
	}

}
