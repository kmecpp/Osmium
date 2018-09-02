package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.GameMode;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.cache.PlayerList;

public class BukkitPlayer implements Player {

	private org.bukkit.entity.Player player;

	public BukkitPlayer(org.bukkit.entity.Player player) {
		if (PlayerList.contains(player.getName())) {
			throw new IllegalStateException("Osmium wrapper already exists for this player!");
		}
		this.player = player;
	}

	@Override
	public org.bukkit.entity.Player getSource() {
		return player;
	}

	@Override
	public String getName() {
		return player.getName();
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
	public boolean isOp() {
		return player.isOp();
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
	public GameMode getGameMode() {
		return GameMode.fromImplementation(player.getGameMode());
	}

	@Override
	public void setGameMode(GameMode mode) {
		player.setGameMode(mode.getGameModeImpl());
	}

	@Override
	public double getHealth() {
		return getHealth();
	}

	@Override
	public void setHealth(double health) {
		player.setHealth(health);
	}

}
