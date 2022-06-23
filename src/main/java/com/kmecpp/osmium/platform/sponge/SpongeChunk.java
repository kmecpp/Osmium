package com.kmecpp.osmium.platform.sponge;

import java.util.Collection;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.platform.SpongeAccess;

public class SpongeChunk implements Chunk {

	private org.spongepowered.api.world.Chunk chunk;

	public SpongeChunk(org.spongepowered.api.world.Chunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public org.spongepowered.api.world.Chunk getSource() {
		return chunk;
	}

	@Override
	public int getX() {
		return chunk.getPosition().getX();
	}

	@Override
	public int getZ() {
		return chunk.getPosition().getZ();
	}

	@Override
	public World getWorld() {
		return SpongeAccess.getWorld(chunk.getWorld());
	}

	@Override
	public Entity[] getEntities() {
		//		return new AbstractCollection<org.spongepowered.api.entity.Entity, Entity>(chunk.getEntities(), SpongeEntity::new);

		Collection<org.spongepowered.api.entity.Entity> spongeEntities = chunk.getEntities();
		Entity[] entities = new Entity[spongeEntities.size()];
		int i = 0;
		for (org.spongepowered.api.entity.Entity entity : spongeEntities) {
			entities[i] = new SpongeEntity(entity);
			i++;
		}
		return entities;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return SpongeAccess.getBlock(chunk.getWorld().getLocation(getX() << 4 | x, y, getZ() << 4 | z));
	}

}
