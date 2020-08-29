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
		sendTitle(CS.XAEB, title, true);
	}

	default void sendTitle(String title, boolean extraLine) {
		sendTitle(CS.XAEB, title, extraLine);
	}

	default void sendTitle(CS colors, String title) {
		sendTitle(colors, title, true);
	}

	default void sendTitle(CS colors, String title, boolean extraLine) {
		send("");
		send(colors.getPrimary() + ChatColor.BOLD.toString() + title);
		send(colors.getSecondary() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------");
		if (extraLine) {
			send("");
		}
	}

	default void sendList(String title, Collection<?> list) {
		sendList(CS.XAEB, title, list);
	}

	default void sendNumberedList(String title, Collection<?> list) {
		sendNumberedList(CS.XAEB, title, list, String::valueOf);
	}

	default <T> void sendNumberedList(String title, Collection<T> list, Function<T, String> serializer) {
		sendNumberedList(CS.XAEB, title, list, serializer);
	}

	default <T> void sendList(String title, Collection<T> list, Function<T, String> serializer) {
		sendList(CS.XAEB, title, list, serializer);
	}

	default void sendList(CS colors, String title, Collection<?> list) {
		sendTitle(colors, title);
		for (Object item : list) {
			sendMessage(colors.getSecondary() + " - " + colors.getPrimary() + item);
		}
	}

	default <T> void sendList(CS colors, String title, Collection<T> list, Function<T, String> serializer) {
		sendTitle(colors, title);
		for (T item : list) {
			sendMessage(colors.getSecondary() + " - " + colors.getPrimary() + serializer.apply(item));
		}
	}

	default <T> void sendNumberedList(CS colors, String title, Collection<T> list, Function<T, String> serializer) {
		sendTitle(colors, title);
		int i = 1;
		for (T item : list) {
			sendMessage("" + colors.getSecondary() + i + ") " + colors.getPrimary() + serializer.apply(item));
			i++;
		}
	}

}
