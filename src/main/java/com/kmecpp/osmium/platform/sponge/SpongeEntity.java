package com.kmecpp.osmium.platform.sponge;

import java.util.UUID;

import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;

import com.flowpowered.math.vector.Vector3d;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.entity.EntityType;
import com.kmecpp.osmium.api.location.Direction;
import com.kmecpp.osmium.api.location.Location;

public class SpongeEntity implements Entity {

	private org.spongepowered.api.entity.Entity entity;

	public SpongeEntity(org.spongepowered.api.entity.Entity entity) {
		this.entity = entity;
	}

	@Override
	public org.spongepowered.api.entity.Entity getSource() {
		return entity;
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}

	@Override
	public World getWorld() {
		return SpongeAccess.getWorld(entity.getWorld());
	}

	@Override
	public String getWorldName() {
		return entity.getWorld().getName();
	}

	@Override
	public String getDisplayName() {
		return entity.get(DisplayNameData.class).get().displayName().get().toString();
	}

	@Override
	public void setDisplayName(String name) {
		entity.get(DisplayNameData.class).get().displayName().set(SpongeAccess.getText(name));
	}

	@Override
	public Location getLocation() {
		return SpongeAccess.getLocation(entity.getLocation());
	}

	@Override
	public boolean setLocation(Location location) {
		return entity.setLocation(location.getImplementation());
	}

	@Override
	public Direction getDirection() {
		return new Direction((float) (entity.getRotation().getY() + 90) % 360, (float) entity.getRotation().getX() * -1);
	}

	@Override
	public void setDirection(Direction direction) {
		entity.setRotation(new Vector3d(direction.getPitch(), direction.getYaw(), 0));
	}

	@Override
	public void setVelocity(int x, int y, int z) {
		entity.setVelocity(new Vector3d(x, y, z));
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		entity.setVelocity(new Vector3d(x, y, z));
	}

	@Override
	public EntityType getType() {
		return SpongeAccess.getEntityType(entity.getType());
	}

}
