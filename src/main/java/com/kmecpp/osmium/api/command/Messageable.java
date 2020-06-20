package com.kmecpp.osmium.api.command;

import java.util.Collection;
import java.util.function.Function;

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

	default void sendList(String title, Collection<?> list) {
		sendList(CS.XEA, title, list);
	}

	default void sendNumberedList(String title, Collection<?> list) {
		sendNumberedList(CS.XEA, title, list);
	}

	default <T> void sendList(String title, Collection<T> list, Function<T, String> prefixer) {
		sendList(CS.XEA, title, list, prefixer);
	}

	default void sendList(CS colors, String title, Collection<?> list) {
		sendTitle(colors, title);
		for (Object item : list) {
			sendMessage(colors.getSecondary() + " - " + colors.getPrimary() + item);
		}
	}

	default <T> void sendList(CS colors, String title, Collection<T> list, Function<T, String> prefixer) {
		sendTitle(colors, title);
		for (T item : list) {
			sendMessage(colors.getSecondary() + " - " + colors.getPrimary() + prefixer.apply(item) + item);
		}
	}

	default void sendNumberedList(CS colors, String title, Collection<?> list) {
		sendTitle(colors, title);
		int i = 1;
		for (Object item : list) {
			sendMessage("" + colors.getSecondary() + i + ") " + colors.getPrimary() + item);
			i++;
		}
	}

}
