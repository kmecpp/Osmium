package com.kmecpp.osmium.api.event.events;

import java.util.Set;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;

public interface PlayerChatEvent extends PlayerEvent, Cancellable {

	String getMessage();

	void setMessage(String message);

	Set<Player> getRecipients();

}
