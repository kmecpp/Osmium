package com.kmecpp.osmium.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.platform.UnsupportedPlatformException;
import com.kmecpp.osmium.platform.bukkit.BukkitPlayer;
import com.kmecpp.osmium.platform.bungee.BungeePlayer;
import com.kmecpp.osmium.platform.sponge.SpongePlayer;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerList {

	private static final HashMap<String, Player> players = new HashMap<>();

	public static void addPlayer(Object sourcePlayer) {
		//		Player wrapper = sourcePlayer instanceof org.bukkit.entity.Player ? new BukkitPlayer((org.bukkit.entity.Player) sourcePlayer)
		//				: sourcePlayer instanceof org.spongepowered.api.entity.living.player.Player ? new SpongePlayer((org.spongepowered.api.entity.living.player.Player) sourcePlayer)
		//						: null;

		Player wrapper = Platform.isBukkit() ? new BukkitPlayer((org.bukkit.entity.Player) sourcePlayer)
				: Platform.isSponge() ? new SpongePlayer((org.spongepowered.api.entity.living.player.Player) sourcePlayer)
						: Platform.isProxy() ? new BungeePlayer((ProxiedPlayer) sourcePlayer) : null;

		if (wrapper == null) {
			throw new IllegalArgumentException("Not a player!");
		}
		players.put(wrapper.getName().toLowerCase(), wrapper);
	}

	public static void removePlayer(String name) {
		players.remove(name.toLowerCase());

		List<Player> offlinePlayers = players.values().stream().filter(p -> !p.isOnline()).collect(Collectors.toList());
		if (offlinePlayers != null && !offlinePlayers.isEmpty()) {
			System.out.println("OFFLINE PLAYERS DETECTED IN OSMIUM PLAYER LIST CACHE: " + offlinePlayers);
		}
	}

	public static boolean contains(String name) {
		return players.containsKey(name.toLowerCase());
	}

	public static Player getPlayer(Object sourcePlayer) {
		Player osmiumPlayer;
		if (Platform.isBukkit()) {
			org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) sourcePlayer;
			osmiumPlayer = getPlayer(bukkitPlayer.getName());
			if (osmiumPlayer == null) {
				osmiumPlayer = new BukkitPlayer(bukkitPlayer);
			}
		} else if (Platform.isSponge()) {
			org.spongepowered.api.entity.living.player.Player spongePlayer = (org.spongepowered.api.entity.living.player.Player) sourcePlayer;
			osmiumPlayer = getPlayer(spongePlayer.getName());
			if (osmiumPlayer == null) {
				osmiumPlayer = new SpongePlayer(spongePlayer);
			}
		} else if (Platform.isProxy()) {
			ProxiedPlayer bungeePlayer = (ProxiedPlayer) sourcePlayer;
			osmiumPlayer = getPlayer(bungeePlayer.getName());
			if (osmiumPlayer == null) {
				osmiumPlayer = new BungeePlayer(bungeePlayer);
			}
		} else {
			throw new UnsupportedPlatformException();
		}
		return osmiumPlayer;
	}

	public static Player getPlayer(String name) {
		return players.get(name.toLowerCase());
	}

	public static Collection<Player> getPlayers() {
		return players.values();
	}

}
