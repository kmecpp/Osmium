package com.kmecpp.osmium.platform.bukkit;

import java.util.UUID;

import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.WorldType;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.location.Location;

public class BukkitWorld implements World {

	private org.bukkit.World world;

	public BukkitWorld(org.bukkit.World world) {
		this.world = world;
	}

	@Override
	public UUID getUniqueId() {
		return world.getUID();
	}

	@Override
	public org.bukkit.World getSource() {
		return world;
	}

	@Override
	public String getName() {
		return world.getName();
	}

	@Override
	public int getHighestYAt(int x, int z) {
		return world.getHighestBlockYAt(x, z);
	}

	@Override
	public void spawnEntity(Location location, EntityType type) {
		world.spawnEntity(location.getImplementation(), type.getImplementation());
	}

	@Override
	public WorldType getType() {
		return WorldType.fromImplementation(world.getEnvironment());
	}

}
