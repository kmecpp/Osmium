package com.kmecpp.osmium.api.command;

import org.bukkit.ChatColor;

public interface Messageable {

	void sendMessage(String message);

	default void sendStyledMessage(String message) {
		sendMessage(Chat.style(message));
	}

	default void sendTitle(String title) {
		sendTitle(CS.XEA, title);
	}

	default void sendTitle(CS colors, String title) {
		sendStyledMessage("");
		sendStyledMessage(colors.getPrimary() + ChatColor.BOLD.toString() + title);
		sendStyledMessage(colors.getSecondary() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------");
		sendStyledMessage("");
	}

}
