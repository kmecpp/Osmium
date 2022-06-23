package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.platform.BukkitAccess;

public class BukkitChunk implements Chunk {

	private org.bukkit.Chunk chunk;

	public BukkitChunk(org.bukkit.Chunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public org.bukkit.Chunk getSource() {
		return chunk;
	}

	@Override
	public int getX() {
		return chunk.getX();
	}

	@Override
	public int getZ() {
		return chunk.getZ();
	}

	@Override
	public World getWorld() {
		return BukkitAccess.getWorld(chunk.getWorld());
	}

	@Override
	public Entity[] getEntities() {
		org.bukkit.entity.Entity[] bukkitEntities = chunk.getEntities();
		Entity[] entities = new Entity[bukkitEntities.length];
		for (int i = 0; i < entities.length; i++) {
			entities[i] = BukkitAccess.getEntity(bukkitEntities[i]);
		}
		return entities;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return BukkitAccess.getBlock(chunk.getBlock(x, y, z));
	}

}
