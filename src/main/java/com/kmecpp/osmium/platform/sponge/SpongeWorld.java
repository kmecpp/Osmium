package com.kmecpp.osmium.platform.sponge;

import java.util.UUID;

import com.kmecpp.osmium.api.World;

public class SpongeWorld implements World {

	private org.spongepowered.api.world.World world;

	public SpongeWorld(org.spongepowered.api.world.World world) {
		this.world = world;
	}

	@Override
	public org.spongepowered.api.world.World getSource() {
		return world;
	}

	@Override
	public UUID getUniqueId() {
		return world.getUniqueId();
	}

	@Override
	public String getName() {
		return world.getName();
	}

	@Override
	public int getHighestYAt(int x, int z) {
		return world.getHighestYAt(x, z);
	}

}
