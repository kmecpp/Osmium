package com.kmecpp.osmium.api;

import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.entity.Player;

public interface User extends Abstraction {

	UUID getUniqueId();

	String getName();

	int getOsmiumId();

	ZoneId getTimeZone();

	boolean isOp();

	long getLastPlayed();

	long getFirstPlayed();

	boolean hasPlayedBefore();

	boolean isOnline();

	default GameProfile getGameProfile() {
		return new GameProfile(getUniqueId(), getName());
	}

	default Optional<Player> getPlayer() {
		return Osmium.getPlayer(getName());
	}

	default <T> T getOrQueryPlayerData(Class<T> cls, SQLDatabase database) {
		Optional<Player> optionalPlayer = getPlayer();
		if (optionalPlayer.isPresent()) {
			return optionalPlayer.get().getData(cls);
		} else {
			return database.get(cls, getUniqueId());
		}
	}

}
