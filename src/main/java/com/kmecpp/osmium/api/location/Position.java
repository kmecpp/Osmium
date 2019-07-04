package com.kmecpp.osmium.api.location;

public class Position extends Vector3d {

	private float pitch;
	private float yaw;

	public Position(double x, double y, double z, float pitch, float yaw) {
		super(x, y, z);
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public static Position fromString(String str) {
		String[] parts = str.split(",");
		return new Position(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
				Float.parseFloat(parts[3]), Float.parseFloat(parts[4]));
	}

	@Override
	public String toString() {
		return getX() + "," + getY() + "," + getZ() + "," + pitch + "," + yaw;
	}

}
