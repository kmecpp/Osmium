package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.World;

public class BukkitLocation implements Location {

	private org.bukkit.Location location;

	public BukkitLocation(org.bukkit.Location location) {
		this.location = location;
	}

	@Override
	public org.bukkit.Location getSource() {
		return location;
	}

	@Override
	public World getWorld() {
		return new BukkitWorld(location.getWorld());
	}

	@Override
	public double getX() {
		return location.getX();
	}

	@Override
	public double getY() {
		return location.getY();
	}

	@Override
	public double getZ() {
		return location.getZ();
	}

}
