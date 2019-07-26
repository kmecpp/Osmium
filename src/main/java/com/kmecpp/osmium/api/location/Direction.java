package com.kmecpp.osmium.api.location;

import org.bukkit.util.Vector;

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

	@SuppressWarnings("unchecked")
	public <T> T toBukkitVector() {
		Vector vector = new Vector();

		double rotX = this.getYaw();
		double rotY = this.getPitch();

		vector.setY(-Math.sin(Math.toRadians(rotY)));

		double xz = Math.cos(Math.toRadians(rotY));

		vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
		vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

		return (T) vector;
	}

	@SuppressWarnings("unchecked")
	public <T> T toSpongeVector() {
		double rotX = this.getYaw();
		double rotY = this.getPitch();

		double y = -Math.sin(Math.toRadians(rotY));

		double xz = Math.cos(Math.toRadians(rotY));

		double x = -xz * Math.sin(Math.toRadians(rotX));
		double z = xz * Math.cos(Math.toRadians(rotX));

		return (T) new Vector3d(x, y, z);
	}

	public static Direction fromString(String str) {
		String[] parts = str.split(",");
		return new Direction(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
	}

	public static Direction fromParts(String pitch, String yaw) {
		return new Direction(Float.parseFloat(pitch), Float.parseFloat(yaw));
	}

}
