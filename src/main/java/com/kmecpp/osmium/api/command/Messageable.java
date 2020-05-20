package com.kmecpp.osmium.api.command;

import java.util.List;

import org.bukkit.ChatColor;

public interface Messageable {

	void sendMessage(String message);

	default void send(String message) {
		sendMessage(Chat.style(message));
	}

	default void sendTitle(String title) {
		sendTitle(CS.XEA, title);
	}

	default void sendTitle(CS colors, String title) {
		send("");
		send(colors.getPrimary() + ChatColor.BOLD.toString() + title);
		send(colors.getSecondary() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------");
		send("");
	}

	default void sendList(String title, List<?> list) {
		sendList(CS.XEA, title, list);
	}

	default void sendList(CS colors, String title, List<?> list) {
		sendTitle(colors, title);
		for (Object item : list) {
			sendMessage(colors.getSecondary() + " - " + colors.getPrimary() + item);
		}
	}

}
