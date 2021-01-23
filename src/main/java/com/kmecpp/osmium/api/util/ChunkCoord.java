package com.kmecpp.osmium.api.util;

public class ChunkCoord {

	private final int x;
	private final int z;

	public ChunkCoord(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		return x * 31 + z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChunkCoord) {
			ChunkCoord coord = (ChunkCoord) obj;
			return x == coord.x && z == coord.z;
		}

		return false;
	}

	@Override
	public String toString() {
		return "ChunkCoord[" + x + ", " + z + "]";
	}

}
