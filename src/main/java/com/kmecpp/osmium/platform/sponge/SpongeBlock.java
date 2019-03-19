package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.location.Location;

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
	public boolean isAir() {
		return block.getBlockType() == BlockTypes.AIR;
	}

	@Override
	public Location getLocation() {
		return SpongeAccess.getLocation(block.getLocatableBlock().get().getLocation());
	}

}
