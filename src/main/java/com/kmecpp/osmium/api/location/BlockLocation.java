package com.kmecpp.osmium.api.location;

public class BlockLocation {

	private String worldName;
	private int x, y, z;

	public BlockLocation(String worldName, int x, int y, int z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
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
	public boolean equals(Object obj) {
		if (obj instanceof BlockLocation) {
			BlockLocation location = (BlockLocation) obj;
			return x == location.x && y == location.y && z == location.z;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return x * y * z;
	}

}
