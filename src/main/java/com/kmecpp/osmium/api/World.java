package com.kmecpp.osmium.api;

import java.util.UUID;

import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.location.Location;

public interface World extends Abstraction {

	UUID getUniqueId();

	String getName();

	int getHighestYAt(int x, int z);

	void spawnEntity(Location location, EntityType type);
	
	WorldType getType();

}
