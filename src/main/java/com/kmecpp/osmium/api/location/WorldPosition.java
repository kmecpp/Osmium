package com.kmecpp.osmium.api.location;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.config.ConfigTypes;
import com.kmecpp.osmium.api.entity.Player;

public class WorldPosition {

	private Location location;
	private Direction direction;

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
		return location;
	}

	public Direction getDirection() {
		return direction;
	}

	public void teleport(Player player) {
		player.setLocation(location);
		player.setDirection(direction);
	}

	@Override
	public String toString() {
		return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ()
				+ "," + direction.getPitch() + "," + direction.getYaw();
	}

	public static WorldPosition fromString(String str) {
		String[] parts = str.split(",");
		return new WorldPosition(
				new Location(Osmium.getWorld(parts[0]).get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])),
				new Direction(Float.parseFloat(parts[4]), Float.parseFloat(parts[5])));
	}

}
