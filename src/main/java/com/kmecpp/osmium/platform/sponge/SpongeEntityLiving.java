package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.entity.projectile.Projectile;

import com.kmecpp.osmium.api.entity.EntityLiving;
import com.kmecpp.osmium.api.location.Direction;

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

	@SuppressWarnings("unchecked")
	@Override
	public void launch(Class<? extends com.kmecpp.osmium.api.Projectile> projectile) {
		entity.launchProjectile((Class<? extends Projectile>) projectile);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void launch(Class<? extends com.kmecpp.osmium.api.Projectile> projectile, Direction direction) {
		entity.launchProjectile((Class<? extends Projectile>) projectile, direction.toSpongeVector());
	}

}
