package com.kmecpp.osmium.platform.bukkit.event.events;

import java.net.InetAddress;
import java.util.UUID;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.platform.BukkitAccess;

public abstract class BukkitPlayerConnectionEvent implements PlayerConnectionEvent {

	public static class BukkitPlayerAuthEvent extends BukkitPlayerConnectionEvent implements PlayerConnectionEvent.Auth {

		private org.bukkit.event.player.AsyncPlayerPreLoginEvent event;

		public BukkitPlayerAuthEvent(org.bukkit.event.player.AsyncPlayerPreLoginEvent event) {
			this.event = event;
		}

		@Override
		public org.bukkit.event.player.AsyncPlayerPreLoginEvent getSource() {
			return event;
		}

		@Override
		public void setCancelled(boolean cancel) {
			event.setLoginResult(cancel
					? org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER
					: org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED);
		}

		@Override
		public boolean isCancelled() {
			return event.getLoginResult() != org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED;
		}

		@Override
		public void setKickMessage(String message) {
			event.setKickMessage(message);
		}

		@Override
		public String getPlayerName() {
			return event.getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getAddress();
		}

	}

	public static class BukkitPlayerLoginEvent extends BukkitPlayerConnectionEvent implements PlayerConnectionEvent.Login {

		private org.bukkit.event.player.PlayerLoginEvent event;

		public BukkitPlayerLoginEvent(org.bukkit.event.player.PlayerLoginEvent event) {
			this.event = event;
		}

		@Override
		public org.bukkit.event.player.PlayerLoginEvent getSource() {
			return event;
		}

		@Override
		public void setCancelled(boolean cancel) {
			event.setResult(cancel
					? org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER
					: org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED);
		}

		@Override
		public boolean isCancelled() {
			return event.getResult() != org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED;
		}

		@Override
		public void setKickMessage(String message) {
			event.setKickMessage(message);
		}

		@Override
		public String getPlayerName() {
			return event.getPlayer().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getPlayer().getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getAddress();
		}

	}

	public static class BukkitPlayerJoinEvent extends BukkitPlayerConnectionEvent implements PlayerConnectionEvent.Join {

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

		@Override
		public String getPlayerName() {
			return event.getPlayer().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getPlayer().getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getPlayer().getAddress().getAddress();
		}

	}

	public static class BukkitPlayerQuitEvent extends BukkitPlayerConnectionEvent implements PlayerConnectionEvent.Quit {

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

		@Override
		public String getPlayerName() {
			return event.getPlayer().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getPlayer().getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getPlayer().getAddress().getAddress();
		}

	}

}
