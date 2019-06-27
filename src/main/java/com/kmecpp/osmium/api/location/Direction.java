package com.kmecpp.osmium.api.location;

public class Direction {

	private final float pitch;
	private final float yaw;

	public Direction(float pitch, float yaw) {
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	@Override
	public String toString() {
		return pitch + "," + yaw;
	}

	public static Direction fromString(String str) {
		String[] parts = str.split(",");
		return new Direction(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
	}

	public static Direction fromParts(String pitch, String yaw) {
		return new Direction(Float.parseFloat(pitch), Float.parseFloat(yaw));
	}

}
