package com.kmecpp.osmium.cache;

import java.util.Collection;
import java.util.HashMap;

import com.kmecpp.osmium.api.entity.Player;

public class PlayerList {

	private static final HashMap<String, Player> players = new HashMap<>();

	public static void addPlayer(Player player) {
		players.put(player.getName().toLowerCase(), player);
	}

	public static void removePlayer(String name) {
		players.remove(name.toLowerCase());
	}

	public static Player getPlayer(String name) {
		return players.get(name.toLowerCase());
	}

	public static Collection<Player> getPlayers() {
		return players.values();
	}

}
