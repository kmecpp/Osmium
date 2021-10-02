package com.kmecpp.osmium.api.event.events;

import java.net.InetAddress;
import java.util.UUID;

import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.EventAbstraction;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerConnectionEvent extends EventAbstraction {

	String getPlayerName();

	UUID getUniqueId();

	InetAddress getAddress();

	public interface Auth extends PlayerConnectionEvent, Cancellable {

		void setKickMessage(String message);

	}

	public interface Login extends PlayerConnectionEvent, Cancellable {

		void setKickMessage(String message);

	}

	public interface Join extends PlayerConnectionEvent, PlayerEvent {
	}

	public interface Quit extends PlayerConnectionEvent, PlayerEvent {
	}

}
