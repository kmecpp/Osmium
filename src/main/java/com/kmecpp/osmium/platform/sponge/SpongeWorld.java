package com.kmecpp.osmium.platform.sponge;

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
	public String getName() {
		return world.getName();
	}

}
