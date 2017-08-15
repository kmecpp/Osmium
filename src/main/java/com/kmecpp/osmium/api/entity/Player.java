package com.kmecpp.osmium.api.entity;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.platform.Abstraction;
import com.kmecpp.osmium.api.platform.Platform;

public abstract class Player implements Abstraction {

	private org.spongepowered.api.entity.living.player.Player spongePlayer;
	private org.bukkit.entity.Player bukkitPlayer;

	public Player(String name) {
		Platform.execute(
				() -> spongePlayer = Sponge.getServer().getPlayer(name).orElse(null),
				() -> bukkitPlayer = Bukkit.getPlayer(name));
	}

	public org.spongepowered.api.entity.living.player.Player getSpongePlayer() {
		return spongePlayer;
	}

	public org.bukkit.entity.Player getBukkitPlayer() {
		return bukkitPlayer;
	}

	//	public Player(org.spongepowered.api.entity.living.player.Player sponge) {
	//
	//	}
	//
	//	public Player(org.bukkit.entity.Player bukkit) {
	//
	//	}

	@Override
	public void getSpongeSource() {

	}

	@Override
	public void getBukkitSource() {
		// TODO Auto-generated method stub

	}

	//	@Port(bukkit = "sendMessage(String)",
	//			sponge = "sendMessage(")
	public void sendMessage(String message) {
		Platform.execute(() -> spongePlayer.sendMessage(Text.of(message)),
				() -> bukkitPlayer.sendMessage(message));
	}
	//	public void sendMessage(String message) {
	//		Platform.execute(() -> spongePlayer.getValue().sendMessage(Text.of(message)),
	//				() -> bukkitPlayer.getValue().sendMessage(message));
	//	}

	public static class FieldHolder<T> {

		private T value;

		public FieldHolder(T value) {
			this.value = value;
		}

		public void getValue(Class<?> c) {

		}

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

	}

	public static interface IPlayer extends PlatformInterface, Abstraction {

		FieldHolder<org.spongepowered.api.entity.living.player.Player> spongePlayer = new FieldHolder<>(null);
		FieldHolder<org.bukkit.entity.Player> bukkitPlayer = new FieldHolder<>(null);

		FieldHolder<?> player = new FieldHolder<>(null);

		@Override
		default void init() {
			Platform.execute(() -> spongePlayer.setValue(null), () -> bukkitPlayer.setValue(null));
		}

		default void sendMessage(String message) {
		}

	}

	public static interface PlatformInterface {

		void init();

	}

}
