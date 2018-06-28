package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.World;

public class BukkitWorld implements World {

	private org.bukkit.World world;

	public BukkitWorld(org.bukkit.World world) {
		this.world = world;
	}

	@Override
	public org.bukkit.World getSource() {
		return world;
	}

	@Override
	public String getName() {
		return world.getName();
	}

}
