package com.kmecpp.osmium.api.util;

public class Coord2D {

	private final int x;
	private final int y;

	public Coord2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coord2D) {
			Coord2D pair = (Coord2D) obj;
			return x == pair.x && y == pair.y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "<" + x + ", " + y + ">";
	}

}
