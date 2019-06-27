package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Material;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.location.Location;

public class BukkitBlock implements Block {

	private org.bukkit.block.Block block;

	public BukkitBlock(org.bukkit.block.Block block) {
		this.block = block;
	}

	@Override
	public org.bukkit.block.Block getSource() {
		return block;
	}

	@Override
	public boolean isAir() {
		return block.getType() == Material.AIR;
	}

	public Location getLocation() {
		return BukkitAccess.getLocation(block.getLocation());
	}

}
