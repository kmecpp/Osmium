package com.kmecpp.osmium.api;

import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import com.kmecpp.osmium.api.platform.Platform;

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
			SPECTATOR.source = org.bukkit.GameMode.SPECTATOR;
		} else if (Platform.isSponge()) {
			SURVIVAL.source = GameModes.SURVIVAL;
			CREATIVE.source = GameModes.CREATIVE;
			ADVENTURE.source = GameModes.ADVENTURE;
			SPECTATOR.source = GameModes.SPECTATOR;
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
