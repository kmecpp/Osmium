package com.kmecpp.osmium.api.util.lib;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.platform.BukkitAccess;
import com.kmecpp.osmium.platform.BungeeAccess;
import com.kmecpp.osmium.platform.SpongeAccess;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerQueue {

	private HashSet<UUID> queueSet = new HashSet<>();
	private ArrayDeque<UUID> queue = new ArrayDeque<>();

	/**
	 * Run a consumer on at least <i>limit</i> Players that are removed from the
	 * queue
	 * 
	 * @param limit
	 *            the maximum number of players to process
	 * @param consumer
	 *            the player processor
	 */
	public void process(int limit, Consumer<Player> consumer) {
		for (int i = 0; i < limit && !queue.isEmpty(); i++) {
			UUID uuid = queue.poll();
			queueSet.remove(uuid);

			if (Platform.isBukkit()) {
				org.bukkit.entity.Player bukkitPlayer = Bukkit.getPlayer(uuid);
				if (bukkitPlayer != null) {
					consumer.accept(BukkitAccess.getPlayer(bukkitPlayer));
				}
			} else if (Platform.isSponge()) {
				Optional<org.spongepowered.api.entity.living.player.Player> spongePlayer = Sponge.getServer().getPlayer(uuid);
				if (spongePlayer.isPresent()) {
					consumer.accept(SpongeAccess.getPlayer(spongePlayer.get()));
				}
			} else if (Platform.isBungeeCord()) {
				ProxiedPlayer proxyPlayer = BungeeCord.getInstance().getPlayer(uuid);
				if (proxyPlayer != null) {
					consumer.accept(BungeeAccess.getPlayer(proxyPlayer));
				}
			}
		}
	}

	/**
	 * Add all online players to the queue that aren't already in the queue
	 */
	public void addAll() {
		if (Platform.isBukkit()) {
			for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
				add(player.getUniqueId());
			}
		} else if (Platform.isSponge()) {
			for (org.spongepowered.api.entity.living.player.Player player : Sponge.getServer().getOnlinePlayers()) {
				add(player.getUniqueId());
			}
		} else if (Platform.isBungeeCord()) {
			for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
				add(player.getUniqueId());
			}
		}
	}

	private void add(UUID uuid) {
		if (!queueSet.contains(uuid)) {
			queue.add(uuid);
		}
	}

}
