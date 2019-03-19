package com.kmecpp.osmium.api;

import com.kmecpp.osmium.api.location.Location;

public interface Block extends Abstraction {

	//	private Location location;
	//
	//	public Location getLocation() {
	//		org.bukkit.block.Block b;
	//		BlockState s;
	//		org.spongepowered.api.world.Location<World> l;
	//		
	//		//		l.getBlock
	//		return location;
	//	}

	Location getLocation();

	boolean isAir();

	default int getX() {
		return getLocation().getBlockX();
	}

	default int getY() {
		return getLocation().getBlockY();
	}

	default int getZ() {
		return getLocation().getBlockZ();
	}

}
