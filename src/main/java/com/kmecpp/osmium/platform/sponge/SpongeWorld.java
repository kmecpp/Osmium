package com.kmecpp.osmium.platform.sponge;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.WrappedCollection;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.WorldType;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.location.Location;

public class SpongeWorld implements World {

	private org.spongepowered.api.world.World world;

	public SpongeWorld(org.spongepowered.api.world.World world) {
		this.world = world;
	}

	@Override
	public org.spongepowered.api.world.World getSource() {
		return world;
	}

	@Override
	public UUID getUniqueId() {
		return world.getUniqueId();
	}

	@Override
	public String getName() {
		return world.getName();
	}

	@Override
	public Path getFolder() {
		return world.getDirectory();
	}

	@Override
	public int getHighestYAt(int x, int z) {
		return world.getHighestYAt(x, z);
	}

	@Override
	public Block getBlock(Location location) {
		return new SpongeBlock(location.<org.spongepowered.api.world.Location<org.spongepowered.api.world.World>> getImplementation());
	}

	@Override
	public void spawnEntity(Location location, EntityType type) {
		world.spawnEntity(world.createEntity((org.spongepowered.api.entity.EntityType) type.getSource(),
				new Vector3d(location.getX(), location.getY(), location.getZ())));
	}

	@Override
	public WorldType getType() {
		return WorldType.fromImplementation(world.getProperties().getDimensionType());
	}

	@Override
	public Location getSpawnLocation() {
		org.spongepowered.api.world.Location<org.spongepowered.api.world.World> spawn = world.getSpawnLocation();
		return new Location(this, spawn.getX(), spawn.getY(), spawn.getZ());
	}

	@Override
	public boolean setSpawnLocation(Location location) {
		world.getProperties().setSpawnPosition(new Vector3i(location.getX(), location.getY(), location.getZ()));
		return true;
	}

	@Override
	public Collection<Entity> getEntities() {
		return new WrappedCollection<>(world.getEntities(), SpongeAccess::getEntity);
	}

}
