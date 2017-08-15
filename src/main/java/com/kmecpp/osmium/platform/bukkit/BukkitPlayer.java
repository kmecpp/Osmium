package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.Player;

public class BukkitPlayer implements Player {

	private org.bukkit.entity.Player player;

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

}
