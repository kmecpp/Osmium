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

}
