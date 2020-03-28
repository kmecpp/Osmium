package com.kmecpp.osmium.api.location;

import org.bukkit.World;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.persistence.Serialization;

public class WorldPosition {

	private Location location;
	private Direction direction;

	static {
		Serialization.register(WorldPosition.class, WorldPosition::fromString);
	}

	public WorldPosition(Player player) {
		this(player.getLocation(), player.getDirection());
	}

	public WorldPosition(Location location, Direction direction) {
		this.location = location;
		this.direction = direction;
	}

	public WorldPosition(com.kmecpp.osmium.api.World world, double x, double y, double z, float pitch, float yaw) {
		this(new Location(world, x, y, z), new Direction(pitch, yaw));
	}

	public Location getLocation() {
		return location;
	}

	public Object asBukkitLocation() {
		return new org.bukkit.Location((World) location.getWorld().getSource(),
				location.getX(), location.getY(), location.getZ(),
				direction.getYaw(), direction.getPitch());
	}

	//	public Location getLocation() {
	//		if (location == null && serialized != null) {
	//			String[] parts = serialized.split(" ");
	//			location = new Location(Osmium.getWorld(parts[0]).get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
	//			this.serialized = null;
	//		}
	//		return location;
	//	}

	public Direction getDirection() {
		return direction;
	}

	public void teleport(Player player) {
		player.setLocation(getLocation());
		player.setDirection(direction);
	}

	@Override
	public String toString() {
		return location.getWorldName()
				+ "," + location.getX() + "," + location.getY() + "," + location.getZ()
				+ "," + direction.getPitch() + "," + direction.getYaw();
	}

	public static WorldPosition fromString(String str) {
		String[] parts = str.split(",");
		return new WorldPosition(Location.fromParts(parts[0], parts[1], parts[2], parts[3]), Direction.fromParts(parts[4], parts[5]));

		//		Optional<World> world = Osmium.getWorld(parts[0]);
		//		WorldPosition position = new WorldPosition(null, new Direction(Float.parseFloat(parts[4]), Float.parseFloat(parts[5])));
		//
		//		if (world.isPresent()) {
		//			position.location = new Location(Osmium.getWorld(parts[0]).get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
		//		} else {
		//			position.serialized = str;
		//		}
		//		return position;

	}

}
