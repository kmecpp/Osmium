package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.location.Location;

public class BukkitBlock implements Block {

	private org.bukkit.block.Block block;

	public BukkitBlock(org.bukkit.block.Block block) {
		this.block = block;
	}

	@Override
	public Object getSource() {
		return block;
	}

	public Location getLocation() {
		return BukkitAccess.getLocation(block.getLocation());
	}

}
