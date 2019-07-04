package com.kmecpp.osmium.api.location;

public class BlockLocation {

	public int x, y, z;

	public BlockLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
