package com.kmecpp.osmium.api.location;

public class Vector3d {

	private double x;
	private double y;
	private double z;

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d add(Vector3d v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3d add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3d subtract(Vector3d v) {
		return add(-v.x, -v.y, -v.z);
	}

	public Vector3d subtract(double x, double y, double z) {
		return add(-x, -y, -z);
	}

	public Vector3d multiply(int n) {
		this.x *= n;
		this.y *= n;
		this.z *= n;
		return this;
	}

	public double dot(Vector3d v) {
		return dot(v.x, v.y, v.z);
	}

	public double dot(double x, double y, double z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Vector3d normalize() {
		double magnitude = magnitude();
		this.x /= magnitude;
		this.y /= magnitude;
		this.z /= magnitude;
		return this;
	}

	public Vector3d invert() {
		return multiply(-1);
	}

	public double distance(Vector3d v) {
		double dx = x - v.x;
		double dy = y - v.y;
		double dz = y - v.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double magnitudeSquared() {
		return x * x + y * y + z * z;
	}

	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

}
