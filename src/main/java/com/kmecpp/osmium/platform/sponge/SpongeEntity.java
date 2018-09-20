package com.kmecpp.osmium.platform.sponge;

import java.util.UUID;

import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Entity;
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

}
