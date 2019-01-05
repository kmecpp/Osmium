package com.kmecpp.osmium.platform.sponge;

import com.kmecpp.osmium.api.entity.EntityLiving;

public class SpongeEntityLiving extends SpongeEntity implements EntityLiving {

	private org.spongepowered.api.entity.living.Living entity;

	public SpongeEntityLiving(org.spongepowered.api.entity.living.Living entity) {
		super(entity);
	}

	@Override
	public double getHealth() {
		return entity.health().get();
	}

	@Override
	public void setHealth(double health) {
		entity.health().set(health);
	}

	@Override
	public double getMaxHealth() {
		return entity.maxHealth().get();
	}

}
