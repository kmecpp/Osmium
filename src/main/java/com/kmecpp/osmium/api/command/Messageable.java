package com.kmecpp.osmium.api.command;

import org.bukkit.ChatColor;

public interface Messageable {

	void sendRawMessage(String message);

	default void sendMessage(String message) {
		sendRawMessage(Chat.style(message));
	}

	default void sendTitle(String title) {
		sendTitle(CS.XEA, title);
	}

	default void sendTitle(CS colors, String title) {
		sendMessage("");
		sendMessage(colors.getPrimary() + ChatColor.BOLD.toString() + title);
		sendMessage(colors.getSecondary() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------");
		sendMessage("");
	}

}
