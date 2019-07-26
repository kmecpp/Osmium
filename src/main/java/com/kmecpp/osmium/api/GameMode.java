package com.kmecpp.osmium.api;

import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import com.kmecpp.osmium.Platform;

public enum GameMode {

	SURVIVAL,
	CREATIVE,
	ADVENTURE,
	SPECTATOR;

	private Object source;

	static {
		if (Platform.isBukkit()) {
			SURVIVAL.source = org.bukkit.GameMode.SURVIVAL;
			CREATIVE.source = org.bukkit.GameMode.CREATIVE;
			ADVENTURE.source = org.bukkit.GameMode.ADVENTURE;
			try {
				SPECTATOR.source = org.bukkit.GameMode.SPECTATOR;
			} catch (Throwable t) {
				//1.7.10
			}
		} else if (Platform.isSponge()) {
			SURVIVAL.source = GameModes.SURVIVAL;
			CREATIVE.source = GameModes.CREATIVE;
			ADVENTURE.source = GameModes.ADVENTURE;
			try {
				SPECTATOR.source = GameModes.SPECTATOR;
			} catch (Throwable t) {
				//1.7.10
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getSource() {
		return (T) source;
	}

	public static GameMode fromSource(Object obj) {
		for (GameMode gameMode : values()) {
			if (gameMode.source == obj) {
				return gameMode;
			}
		}
		return null;
	}

}
