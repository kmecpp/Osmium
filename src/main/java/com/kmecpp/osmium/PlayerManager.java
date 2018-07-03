package com.kmecpp.osmium;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.service.Service;

public class PlayerManager implements Service {

	private static HashMap<String, Player> players = new HashMap<>();

	public static Collection<Player> getPlayers() {
		return players.values();
	}

	public static Optional<Player> get(String name) {
		return Optional.ofNullable(players.get(name));
	}

	public static Player fromBukkitPlayer(org.bukkit.entity.Player player) {
		return players.get(player.getName());
	}

	public static Player fromSpongePlayer(org.spongepowered.api.entity.living.player.Player player) {
		return players.get(player.getName());
	}

	@Listener
	public void onPlayerLogin(PlayerLoginEvent e) {

	}

	@Listener
	public void onPlayerQuit(PlayerQuitEvent e) {

	}

}
