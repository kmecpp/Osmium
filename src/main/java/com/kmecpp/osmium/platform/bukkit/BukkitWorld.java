package com.kmecpp.osmium.platform.bukkit;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.WrappedCollection;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.Chunk;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.WorldType;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.location.Location;

public class BukkitWorld implements World {

	private org.bukkit.World world;

	public BukkitWorld(org.bukkit.World world) {
		this.world = world;
	}

	@Override
	public UUID getUniqueId() {
		return world.getUID();
	}

	@Override
	public org.bukkit.World getSource() {
		return world;
	}

	@Override
	public String getName() {
		return world.getName();
	}

	@Override
	public Path getFolder() {
		return world.getWorldFolder().toPath();
	}

	@Override
	public int getHighestYAt(int x, int z) {
		return world.getHighestBlockYAt(x, z);
	}

	@Override
	public Block getBlock(Location location) {
		return new BukkitBlock(location.<org.bukkit.Location> getSource().getBlock());
	}

	@Override
	public Chunk getChunk(Location location) {
		return new BukkitChunk(world.getChunkAt(location.getBlockX(), location.getBlockY()));
	}

	@Override
	public void spawnEntity(Location location, EntityType type) {
		world.spawnEntity(location.getSource(), (org.bukkit.entity.EntityType) type.getSource());
	}

	@Override
	public Location getSpawnLocation() {
		org.bukkit.Location spawn = world.getSpawnLocation();
		return new Location(this, spawn.getX(), spawn.getY(), spawn.getZ());
	}

	@Override
	public boolean setSpawnLocation(Location location) {
		return world.setSpawnLocation(new org.bukkit.Location(world, location.getX(), location.getY(), location.getZ()));
	}

	@Override
	public WorldType getType() {
		return WorldType.fromImplementation(world.getEnvironment());
	}

	@Override
	public Collection<Entity> getEntities() {
		return new WrappedCollection<>(world.getEntities(), BukkitAccess::getEntity);
	}

	@Override
	public int hashCode() {
		return world.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BukkitWorld) {
			return world.equals(((BukkitWorld) obj).world);
		}
		return false;
	}

}
