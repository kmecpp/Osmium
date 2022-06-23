package com.kmecpp.osmium.platform.bungee.events;

import java.net.InetAddress;
import java.util.UUID;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.platform.BungeeAccess;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;

public abstract class BungeePlayerConnectionEvent implements PlayerConnectionEvent {

	public static class BungeePlayerAuthEvent extends BungeePlayerConnectionEvent implements PlayerConnectionEvent.Auth {

		private LoginEvent event; //PreLoginEvent exists but UUID is not accessible when it is called. Use LoginEvent instead? If so auth is post-login for Bungee and pre for others.

		public BungeePlayerAuthEvent(LoginEvent event) {
			this.event = event;
		}

		@Override
		public LoginEvent getSource() {
			return event;
		}

		@Override
		public void setCancelled(boolean cancel) {
			event.setCancelled(cancel);
		}

		@Override
		public boolean isCancelled() {
			return event.isCancelled();
		}

		@Override
		public void setKickMessage(String message) {
			event.setCancelReason(TextComponent.fromLegacyText(message));
		}

		@Override
		public String getPlayerName() {
			return event.getConnection().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getConnection().getUniqueId();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InetAddress getAddress() {
			return event.getConnection().getAddress().getAddress();
		}

	}

	public static class BungeePlayerLoginEvent extends BungeePlayerConnectionEvent implements PlayerConnectionEvent.Login {

		private LoginEvent event;

		public BungeePlayerLoginEvent(LoginEvent event) {
			this.event = event;
		}

		@Override
		public LoginEvent getSource() {
			return event;
		}

		@Override
		public void setCancelled(boolean cancel) {
			event.setCancelled(cancel);
		}

		@Override
		public boolean isCancelled() {
			return event.isCancelled();
		}

		@Override
		public void setKickMessage(String message) {
			event.setCancelReason(TextComponent.fromLegacyText(message));
		}

		@Override
		public String getPlayerName() {
			return event.getConnection().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getConnection().getUniqueId();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InetAddress getAddress() {
			return event.getConnection().getAddress().getAddress();
		}

	}

	public static class BungeePlayerJoinEvent extends BungeePlayerLoginEvent implements PlayerConnectionEvent.Join {

		public BungeePlayerJoinEvent(LoginEvent event) {
			super(event);
		}

		@Override
		public Player getPlayer() {
			return null;
		}

	}

	public static class BungeePlayerQuitEvent extends BungeePlayerConnectionEvent implements PlayerConnectionEvent.Quit {

		private PlayerDisconnectEvent event;

		public BungeePlayerQuitEvent(PlayerDisconnectEvent event) {
			this.event = event;
		}

		@Override
		public PlayerDisconnectEvent getSource() {
			return event;
		}

		@Override
		public Player getPlayer() {
			return BungeeAccess.getPlayer(event.getPlayer());
		}

		@Override
		public UUID getUniqueId() {
			return event.getPlayer().getUniqueId();
		}

		@Override
		public String getPlayerName() {
			return event.getPlayer().getName();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InetAddress getAddress() {
			return event.getPlayer().getAddress().getAddress();
		}

	}

}
