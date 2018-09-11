package com.kmecpp.osmium.api;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.extent.Extent;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.cache.WorldList;

public class Location {

	private final World world;
	private final double x;
	private final double y;
	private final double z;

	public Location(World world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public World getWorld() {
		return world;
	}

	public double getX() {
		return x;
	}

	public int getBlockX() {
		return (int) x;
	}

	public double getY() {
		return y;
	}

	public int getBlockY() {
		return (int) y;
	}

	public double getZ() {
		return z;
	}

	public int getBlockZ() {
		return (int) z;
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
			return (T) new org.bukkit.Location(Bukkit.getWorld(world.getName()), x, y, z);
		} else if (Platform.isSponge()) {
			return (T) new org.spongepowered.api.world.Location<Extent>(Sponge.getServer().getWorld(world.getName()).get(), x, y, z);
		} else {
			return null;
		}
	}

}
