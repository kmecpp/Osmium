package com.kmecpp.osmium.api.location;

import java.util.Optional;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.config.ConfigTypes;
import com.kmecpp.osmium.api.entity.Player;

public class WorldPosition {

	private Location location;
	private Direction direction;

	private String serialized;

	static {
		ConfigTypes.register(WorldPosition.class, WorldPosition::fromString);
	}

	public WorldPosition(Player player) {
		this(player.getLocation(), player.getDirection());
	}

	public WorldPosition(Location location, Direction direction) {
		this.location = location;
		this.direction = direction;
	}

	public Location getLocation() {
		if (location == null && serialized != null) {
			String[] parts = serialized.split(" ");
			location = new Location(Osmium.getWorld(parts[0]).get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
			this.serialized = null;
		}
		return location;
	}

	public Direction getDirection() {
		return direction;
	}

	public void teleport(Player player) {
		player.setLocation(getLocation());
		player.setDirection(direction);
	}

	@Override
	public String toString() {
		return location == null ? serialized : (location.getWorld().getName()
				+ "," + location.getX() + "," + location.getY() + "," + location.getZ()
				+ "," + direction.getPitch() + "," + direction.getYaw());
	}

	public static WorldPosition fromString(String str) {
		String[] parts = str.split(",");

		Optional<World> world = Osmium.getWorld(parts[0]);
		WorldPosition position = new WorldPosition(null, new Direction(Float.parseFloat(parts[4]), Float.parseFloat(parts[5])));

		if (world.isPresent()) {
			position.location = new Location(Osmium.getWorld(parts[0]).get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
		} else {
			position.serialized = str;
		}
		return position;

	}

}
