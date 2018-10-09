package com.kmecpp.osmium.cache;

import java.util.Collection;
import java.util.HashMap;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.platform.bukkit.BukkitPlayer;
import com.kmecpp.osmium.platform.sponge.SpongePlayer;

public class PlayerList {

	private static final HashMap<String, Player> players = new HashMap<>();

	public static void addPlayer(Object sourcePlayer) {
		Player wrapper = sourcePlayer instanceof org.bukkit.entity.Player ? new BukkitPlayer((org.bukkit.entity.Player) sourcePlayer)
				: sourcePlayer instanceof org.spongepowered.api.entity.living.player.Player ? new SpongePlayer((org.spongepowered.api.entity.living.player.Player) sourcePlayer)
						: null;

		if (wrapper == null) {
			throw new IllegalArgumentException("Not a player!");
		}
		players.put(wrapper.getName().toLowerCase(), wrapper);
	}

	public static void removePlayer(String name) {
		players.remove(name.toLowerCase());
	}

	public static boolean contains(String name) {
		return players.containsKey(name);
	}

	public static Player getPlayer(String name) {
		return players.get(name.toLowerCase());
	}

	public static Collection<Player> getPlayers() {
		return players.values();
	}

}
