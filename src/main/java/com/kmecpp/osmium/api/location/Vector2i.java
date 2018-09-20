package com.kmecpp.osmium.api.location;

public class Vector2i implements Cloneable {

	private int x;
	private int y;

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Vector2i add(Vector2i v) {
		return add(v.x, v.y);
	}

	public Vector2i add(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2i subtract(Vector2i v) {
		return add(-v.x, -v.y);
	}

	public Vector2i subtract(int x, int y) {
		return add(-x, -y);
	}

	public Vector2i multiply(int n) {
		this.x *= n;
		this.y *= n;
		return this;
	}

	public int dot(Vector2i v) {
		return dot(v.x, v.y);
	}

	public int dot(int x, int y) {
		return this.x * x + this.y * y;
	}

	public Vector2i normalize() {
		double magnitude = magnitude();
		this.x /= magnitude;
		this.y /= magnitude;
		return this;
	}

	public double distance(Vector2i v) {
		int dx = x - v.x;
		int dy = y - v.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public Vector2i invert() {
		return multiply(-1);
	}

	public double magnitudeSquared() {
		return x * x + y * y;
	}

	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}

	@Override
	public Vector2i clone() {
		return new Vector2i(x, y);
	}

	@Override
	public int hashCode() {
		return x ^ y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector2i) {
			Vector2i v = (Vector2i) obj;
			return x == v.x && y == v.y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "<" + x + ", " + y + ">";
	}

}
