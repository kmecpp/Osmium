package com.kmecpp.osmium.platform.sponge.event.events;

import java.net.InetAddress;
import java.util.UUID;

import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.platform.SpongeAccess;

public class SpongePlayerConnectEvent implements PlayerConnectionEvent {

	private ClientConnectionEvent event;

	@Override
	public ClientConnectionEvent getSource() {
		return event;
	}

	@Override
	public String getPlayerName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UUID getUniqueId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InetAddress getAddress() {
		throw new UnsupportedOperationException();
	}

	public static class SpongePlayerAuthEvent implements PlayerConnectionEvent.Auth {

		private ClientConnectionEvent.Auth event;

		public SpongePlayerAuthEvent(ClientConnectionEvent.Auth event) {
			this.event = event;
		}

		@Override
		public ClientConnectionEvent.Auth getSource() {
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
			event.setMessage(Text.of(message));
		}

		@Override
		public String getPlayerName() {
			return event.getProfile().getName().orElse("");
		}

		@Override
		public UUID getUniqueId() {
			return event.getProfile().getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getConnection().getAddress().getAddress();
		}

	}

	public static class SpongePlayerLoginEvent implements PlayerConnectionEvent.Login {

		private ClientConnectionEvent.Login event;

		public SpongePlayerLoginEvent(ClientConnectionEvent.Login event) {
			this.event = event;
		}

		@Override
		public ClientConnectionEvent.Login getSource() {
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
			event.setMessage(Text.of(message));
		}

		@Override
		public String getPlayerName() {
			return event.getTargetUser().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getTargetUser().getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getConnection().getAddress().getAddress();
		}

	}

	public static class SpongePlayerJoinEvent implements PlayerConnectionEvent.Join {

		private ClientConnectionEvent.Join event;

		public SpongePlayerJoinEvent(ClientConnectionEvent.Join event) {
			this.event = event;
		}

		@Override
		public ClientConnectionEvent.Join getSource() {
			return event;
		}

		@Override
		public Player getPlayer() {
			return SpongeAccess.getPlayer(event.getTargetEntity());
		}

		@Override
		public String getPlayerName() {
			return event.getTargetEntity().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getTargetEntity().getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getTargetEntity().getConnection().getAddress().getAddress();
		}

	}

	public static class SpongePlayerQuitEvent implements PlayerConnectionEvent.Quit {

		private ClientConnectionEvent.Disconnect event;

		public SpongePlayerQuitEvent(ClientConnectionEvent.Disconnect event) {
			this.event = event;
		}

		@Override
		public ClientConnectionEvent.Disconnect getSource() {
			return event;
		}

		@Override
		public Player getPlayer() {
			return SpongeAccess.getPlayer(event.getTargetEntity());
		}

		@Override
		public String getPlayerName() {
			return event.getTargetEntity().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getTargetEntity().getUniqueId();
		}

		@Override
		public InetAddress getAddress() {
			return event.getTargetEntity().getConnection().getAddress().getAddress();
		}

	}

}
