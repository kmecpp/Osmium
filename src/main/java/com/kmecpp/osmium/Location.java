package com.kmecpp.osmium;

import com.kmecpp.osmium.api.World;

public class Location {

	private final World world;
	private final double x;
	private final double y;
	private final double z;

	public Location(World world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public World getWorld() {
		return world;
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
