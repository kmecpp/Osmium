package com.kmecpp.osmium.platform.sponge;

import com.kmecpp.osmium.api.Location;
import com.kmecpp.osmium.api.World;

public class SpongeLocation implements Location {

	private org.spongepowered.api.world.Location<org.spongepowered.api.world.World> location;

	public SpongeLocation(org.spongepowered.api.world.Location<org.spongepowered.api.world.World> location) {
		this.location = location;
	}

	@Override
	public org.spongepowered.api.world.Location<org.spongepowered.api.world.World> getSource() {
		return location;
	}

	@Override
	public World getWorld() {
		return new SpongeWorld(location.getExtent());
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
