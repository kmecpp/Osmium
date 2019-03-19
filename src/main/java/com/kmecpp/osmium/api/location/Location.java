package com.kmecpp.osmium.api.location;

import org.spongepowered.api.world.extent.Extent;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.config.ConfigTypes;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.cache.WorldList;

public class Location {

	private final World world;
	private final double x;
	private final double y;
	private final double z;

	public Location(World world, double x, double y, double z) {
		if (world == null) {
			throw new IllegalArgumentException("Invalid world: " + world);
		}
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	static {
		ConfigTypes.register(Location.class, Location::fromString);
	}

	public World getWorld() {
		return world;
	}

	public String getWorldName() {
		return world.getName();
	}

	public Block getBlock() {
		return world.getBlock(this);
	}

	public double getX() {
		return x;
	}

	public int getBlockX() {
		return (int) Math.floor(x);
	}

	public double getY() {
		return y;
	}

	public int getBlockY() {
		return (int) Math.floor(y);
	}

	public double getZ() {
		return z;
	}

	public int getBlockZ() {
		return (int) Math.floor(z);
	}

	public Location add(double x, double y, double z) {
		return new Location(this.world, this.x + x, this.y + y, this.z + z);
	}

	public Location getBlockTopCenter() {
		return new Location(world, ((int) x) + 0.5, y, ((int) z) + 0.5);
	}

	public double distance(Location location) {
		return Math.sqrt(Math.pow(x - location.x, 2) + Math.pow(y - location.y, 2) + Math.pow(z - location.z, 2));
	}

	public static Location fromString(String str) {
		String[] parts = str.split(",");
		return new Location(WorldList.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
	}

	@Override
	public String toString() {
		return world.getName() + "," + x + "," + y + "," + z;
	}

	@SuppressWarnings("unchecked")
	public <T> T getImplementation() {
		if (Platform.isBukkit()) {
			return (T) new org.bukkit.Location((org.bukkit.World) world.getSource(), x, y, z);
		} else if (Platform.isSponge()) {
			return (T) new org.spongepowered.api.world.Location<Extent>((Extent) world.getSource(), x, y, z);
		} else {
			return null;
		}
	}

}
