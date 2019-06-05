package com.kmecpp.osmium.api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.location.Location;

public interface World extends Abstraction {

	UUID getUniqueId();

	String getName();

	int getHighestYAt(int x, int z);

	void spawnEntity(Location location, EntityType type);

	WorldType getType();

	Location getSpawnLocation();

	boolean setSpawnLocation(Location location);

	Path getFolder();

	Collection<Entity> getEntities();

	Block getBlock(Location location);

	Chunk getChunk(Location location);

	default Collection<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<>();
		for (Player player : Osmium.getOnlinePlayers()) {
			if (player.getWorld() == this) {
				players.add(player);
			}
		}
		return players;
	}

}
