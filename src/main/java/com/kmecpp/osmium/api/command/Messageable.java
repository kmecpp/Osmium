package com.kmecpp.osmium.api.command;

public interface Messageable {

	default void sendMessage(String message) {
		sendRawMessage(Chat.style(message));
	}

	void sendRawMessage(String message);

}
