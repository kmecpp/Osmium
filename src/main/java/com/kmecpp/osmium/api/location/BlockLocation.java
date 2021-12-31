package com.kmecpp.osmium.api.location;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.extent.Extent;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.persistence.Serialization;

public class BlockLocation {

	private String worldName;
	private int x, y, z;

	public BlockLocation(String worldName, int x, int y, int z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	static {
		Serialization.register(BlockLocation.class, BlockLocation::fromString);
	}

	public String getWorldName() {
		return worldName;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		return (((31 + worldName.hashCode()) * 31 + Integer.hashCode(x)) * 31 + Integer.hashCode(y)) * 31 + Integer.hashCode(z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockLocation) {
			BlockLocation other = (BlockLocation) obj;
			return x == other.x && y == other.y && z == other.z && worldName.equals(other.worldName);
		}
		return false;
	}

	@Override
	public String toString() {
		return getWorldName() + "," + x + "," + y + "," + z;
	}

	public static BlockLocation fromString(String str) {
		String[] parts = str.split(",");
		return new BlockLocation(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
	}

	@SuppressWarnings("unchecked")
	public static BlockLocation fromImplementation(Object implementation) {
		if (Platform.isBukkit()) {
			org.bukkit.Location l = (org.bukkit.Location) implementation;
			return new BlockLocation(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
		} else if (Platform.isSponge()) {
			org.spongepowered.api.world.Location<org.spongepowered.api.world.World> l = (org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) implementation;
			return new BlockLocation(l.getExtent().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
		}
		return null;
	}

	public org.bukkit.Location asBukkitLocation() {
		return new org.bukkit.Location(Bukkit.getWorld(worldName), x, y, z);
	}

	public org.spongepowered.api.world.Location<Extent> asSpongeLocation() {
		return new org.spongepowered.api.world.Location<Extent>((Extent) Sponge.getServer().getWorld(worldName).get(), x, y, z);
	}

}
