package com.kmecpp.osmium.api.entity;

import java.util.UUID;

import com.kmecpp.osmium.api.Abstraction;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.location.Position;
import com.kmecpp.osmium.api.location.WorldPosition;

public interface Entity extends Abstraction {

	UUID getUniqueId();

	World getWorld();

	String getWorldName();

	String getDisplayName();

	void setDisplayName(String name);

	Location getLocation();

	boolean setLocation(Location location);

	Direction getDirection();

	void setDirection(Direction direction);

	EntityType getType();

	default WorldPosition getWorldPosition() {
		return new WorldPosition(getLocation(), getDirection());
	}

	default Position getPosition() {
		Location location = getLocation();
		Direction direction = getDirection();
		return new Position(location.getX(), location.getY(), location.getZ(), direction.getPitch(), direction.getYaw());
	}

	default void setPosition(WorldPosition position) {
		setLocation(position.getLocation());
		setDirection(position.getDirection());
	}

	default void sendToSpawn() {
		setLocation(getWorld().getSpawnLocation().add(0.5, 0, 0.5));
	}

	//	default SerializableLocation getBlockPosition() {
	//		return new SerializableLocation(getLocation());
	//	}

}
