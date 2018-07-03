package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.world.World;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Location;

public class SpongeBlock implements Block {

	private org.spongepowered.api.world.Location<World> block;

	public SpongeBlock(org.spongepowered.api.world.Location<World> block) {
		this.block = block;
	}

	@Override
	public org.spongepowered.api.world.Location<World> getSource() {
		return block;
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(block);
	}

	@Override
	public int getX() {
		return block.getBlockX();
	}

	@Override
	public int getY() {
		return block.getBlockY();
	}

	@Override
	public int getZ() {
		return block.getBlockZ();
	}

}
