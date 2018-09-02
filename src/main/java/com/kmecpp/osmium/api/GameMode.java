package com.kmecpp.osmium.api;

import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import com.kmecpp.osmium.api.platform.Platform;

public enum GameMode {

	SURVIVAL,
	CREATIVE,
	ADVENTURE,
	SPECTATOR;

	private Object gameModeImpl;

	static {
		if (Platform.isBukkit()) {
			SURVIVAL.gameModeImpl = org.bukkit.GameMode.SURVIVAL;
			CREATIVE.gameModeImpl = org.bukkit.GameMode.CREATIVE;
			ADVENTURE.gameModeImpl = org.bukkit.GameMode.ADVENTURE;
			SPECTATOR.gameModeImpl = org.bukkit.GameMode.SPECTATOR;
		} else if (Platform.isSponge()) {
			SURVIVAL.gameModeImpl = GameModes.SURVIVAL;
			CREATIVE.gameModeImpl = GameModes.CREATIVE;
			ADVENTURE.gameModeImpl = GameModes.ADVENTURE;
			SPECTATOR.gameModeImpl = GameModes.SPECTATOR;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getGameModeImpl() {
		return (T) gameModeImpl;
	}

	public static GameMode fromImplementation(Object obj) {
		for (GameMode gameMode : values()) {
			if (gameMode.gameModeImpl == obj) {
				return gameMode;
			}
		}
		return null;
	}

}
