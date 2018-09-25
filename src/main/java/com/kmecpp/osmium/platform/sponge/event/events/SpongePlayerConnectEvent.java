package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;

public class SpongePlayerConnectEvent {

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
		public String getPlayerName() {
			return event.getProfile().getName().orElseGet(String::new);
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
		public String getPlayerName() {
			return event.getTargetUser().getName();
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

	}

}
