package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;

public class BukkitPlayerConnectEvent {

	public static class BukkitPlayerAuthEvent implements PlayerConnectionEvent.Auth {

		private org.bukkit.event.player.AsyncPlayerPreLoginEvent event;

		public BukkitPlayerAuthEvent(org.bukkit.event.player.AsyncPlayerPreLoginEvent event) {
			this.event = event;
		}

		@Override
		public org.bukkit.event.player.AsyncPlayerPreLoginEvent getSource() {
			return event;
		}

	}

	public static class BukkitPlayerLoginEvent implements PlayerConnectionEvent.Login {

		private org.bukkit.event.player.PlayerLoginEvent event;

		public BukkitPlayerLoginEvent(org.bukkit.event.player.PlayerLoginEvent event) {
			this.event = event;
		}

		@Override
		public org.bukkit.event.player.PlayerLoginEvent getSource() {
			return event;
		}

	}

	public static class BukkitPlayerJoinEvent implements PlayerConnectionEvent.Join {

		private org.bukkit.event.player.PlayerJoinEvent event;

		public BukkitPlayerJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
			this.event = event;
		}

		@Override
		public org.bukkit.event.player.PlayerJoinEvent getSource() {
			return event;
		}

		@Override
		public Player getPlayer() {
			return BukkitAccess.getPlayer(event.getPlayer());
		}

	}

	public static class BukkitPlayerQuitEvent implements PlayerConnectionEvent.Quit {

		private org.bukkit.event.player.PlayerQuitEvent event;

		public BukkitPlayerQuitEvent(org.bukkit.event.player.PlayerQuitEvent event) {
			this.event = event;
		}

		@Override
		public org.bukkit.event.player.PlayerQuitEvent getSource() {
			return event;
		}

		@Override
		public Player getPlayer() {
			return BukkitAccess.getPlayer(event.getPlayer());
		}

	}

}
