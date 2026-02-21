package com.kmecpp.osmium.api.location;

public class Vector3i {

	public int x, y, z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i add(Vector3i v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3i add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3i subtract(Vector3i v) {
		return add(-v.x, -v.y, -v.z);
	}

	public Vector3i subtract(int x, int y, int z) {
		return add(-x, -y, -z);
	}

	public Vector3i multiply(int n) {
		this.x *= n;
		this.y *= n;
		this.z *= n;
		return this;
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

	@Override
	public String toString() {
		return "<" + x + ", " + y + ", " + z + ">";
	}

}
