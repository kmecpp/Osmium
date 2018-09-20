package com.kmecpp.osmium.api;

import com.kmecpp.osmium.api.entity.Entity;

public interface Chunk extends Abstraction {

	int getX();

	int getZ();

	World getWorld();

	Entity[] getEntities();

	Block getBlock(int x, int y, int z);

}
