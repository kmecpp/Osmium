package com.kmecpp.osmium.api.location;

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

}
