package com.kmecpp.osmium.platform.bukkit;

import java.util.UUID;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Entity;
import com.kmecpp.osmium.api.location.Location;

public class BukkitEntity implements Entity {

	private org.bukkit.entity.Entity entity;

	public BukkitEntity(org.bukkit.entity.Entity entity) {
		this.entity = entity;
	}

	@Override
	public org.bukkit.entity.Entity getSource() {
		return entity;
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}

	@Override
	public World getWorld() {
		return BukkitAccess.getWorld(entity.getWorld());
	}

	@Override
	public String getDisplayName() {
		return entity.getCustomName();
	}

	@Override
	public void setDisplayName(String name) {
		entity.setCustomName(name);
	}

	@Override
	public Location getLocation() {
		return BukkitAccess.getLocation(entity.getLocation());
	}

}
