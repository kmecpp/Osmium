package com.kmecpp.osmium.api.command;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Messageable {

	void sendMessage(String message);

	default void send(String message) {
		sendMessage(Chat.style(message));
	}

	default void sendNotification(String message) {
		sendNotification(CS.XAE, message);
	}

	default void sendNotification(CS colors, String message) {
		sendMessage(colors.getSecondary() + Chat.BOLD.toString() + Chat.STRIKETHROUGH + "-------------------------------------------"); //43
		sendMessage("");
		sendMessage(colors.getPrimary() + message);
		sendMessage("");
		sendMessage(colors.getSecondary() + Chat.BOLD.toString() + Chat.STRIKETHROUGH + "-------------------------------------------");
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
		send(colors.getPrimary() + Chat.BOLD.toString() + title);
		send(colors.getSecondary() + Chat.BOLD.toString() + Chat.STRIKETHROUGH + "----------------------------------------");
		if (extraLine) {
			send("");
		}
	}

	default void sendList(Collection<?> list) {
		sendList(CS.XAEB, list, String::valueOf);
	}

	default void sendList(CS colors, Collection<?> list) {
		sendList(colors, list, String::valueOf);
	}

	default <T> void sendList(Collection<T> list, Function<T, String> serializer) {
		sendList(CS.XAEB, list, serializer);
	}

	default <K, V> void sendList(Map<K, V> map, BiFunction<K, V, String> serializer) {
		sendList(CS.XAEB, map, serializer);
	}

	default <K, V> void sendList(CS colors, Map<K, V> map, BiFunction<K, V, String> serializer) {
		sendList(colors, map.entrySet(), entry -> serializer.apply(entry.getKey(), entry.getValue()));
	}

	default <T> void sendList(CS colors, Collection<T> list, Function<T, String> serializer) {
		for (T item : list) {
			sendMessage(colors.getSecondary() + " - " + colors.getPrimary() + serializer.apply(item));
		}
	}

	default void sendNumberedList(Collection<?> list) {
		sendNumberedList(CS.XAEB, list, String::valueOf);
	}

	default <T> void sendNumberedList(Collection<T> list, Function<T, String> serializer) {
		sendNumberedList(CS.XAEB, list, serializer);
	}

	default <T> void sendNumberedList(CS colors, Collection<T> list, Function<T, String> serializer) {
		int i = 1;
		for (T item : list) {
			sendMessage("" + colors.getSecondary() + i + ") " + colors.getPrimary() + serializer.apply(item));
			i++;
		}
	}

}
