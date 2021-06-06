package com.kmecpp.osmium.api.location;

public class Vector3i {

	public int x, y, z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector3i) {
			Vector3i vector = (Vector3i) obj;
			return x == vector.x && y == vector.y && z == vector.z;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return x * y * z;
	}

}
