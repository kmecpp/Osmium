package com.kmecpp.osmium.api.location;

import org.spongepowered.api.world.extent.Extent;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.cache.WorldList;

public class Location {

	//	private World world;
	private String worldName;

	private final double x;
	private final double y;
	private final double z;

	public Location(World world, double x, double y, double z) {
		if (world == null) {
			throw new IllegalArgumentException("Invalid world: " + world);
		}
		//		this.world = world;
		this.worldName = world.getName().intern();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location(String worldName, double x, double y, double z) {
		//		this.world = Osmium.getWorld(worldName).orElse(null);
		this.worldName = worldName.intern();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	static {
		Serialization.register(Location.class, Location::fromString);
	}

	public World getWorld() {
		return WorldList.getWorld(worldName);
		//		if (world == null) {
		//			world = WorldList.getWorld(worldName);
		//		}
		//		return world;
	}

	public String getWorldName() {
		return worldName;
		//		if (world == null) {
		//			return worldName;
		//		}
		//		return world.getName();
	}

	public Block getBlock() {
		return getWorld().getBlock(this);
	}

	public Chunk getChunk() {
		return getWorld().getChunk(this);
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
		return new Location(getWorld(), this.x + x, this.y + y, this.z + z);
	}

	public Location getBlockTopCenter() {
		return new Location(getWorld(), ((int) x) + 0.5, y, ((int) z) + 0.5);
	}

	public double distance(Location location) {
		return Math.sqrt(Math.pow(x - location.x, 2) + Math.pow(y - location.y, 2) + Math.pow(z - location.z, 2));
	}

	public static Location fromString(String str) {
		String[] parts = str.split(",");
		return new Location(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
	}

	public static Location fromParts(String world, String x, String y, String z) {
		return new Location(world, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
	}

	@Override
	public String toString() {
		return getWorldName() + "," + x + "," + y + "," + z;
	}

	@SuppressWarnings("unchecked")
	public <T> T getSource() {
		if (Platform.isBukkit()) {
			return (T) new org.bukkit.Location((org.bukkit.World) getWorld().getSource(), x, y, z);
		} else if (Platform.isSponge()) {
			return (T) new org.spongepowered.api.world.Location<Extent>((Extent) getWorld().getSource(), x, y, z);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Location fromImplementation(Object impl) {
		if (Platform.isBukkit()) {
			org.bukkit.Location l = (org.bukkit.Location) impl;
			return new Location(BukkitAccess.getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
		} else if (Platform.isSponge()) {
			org.spongepowered.api.world.Location<org.spongepowered.api.world.World> l = (org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) impl;
			return new Location(SpongeAccess.getWorld(l.getExtent()), l.getX(), l.getY(), l.getZ());
		} else {
			return null;
		}
	}

}
