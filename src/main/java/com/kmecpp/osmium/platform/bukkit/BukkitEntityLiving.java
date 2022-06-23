package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

import com.kmecpp.osmium.api.entity.EntityLiving;
import com.kmecpp.osmium.api.location.Direction;

public class BukkitEntityLiving extends BukkitEntity implements EntityLiving {

	private LivingEntity entity;

	public BukkitEntityLiving(LivingEntity entity) {
		super(entity);
		this.entity = entity;
	}

	@Override
	public LivingEntity getSource() {
		return entity;
	}

	@Override
	public double getHealth() {
		return entity.getHealth();
	}

	@Override
	public void setHealth(double health) {
		entity.setHealth(health);
	}

	@SuppressWarnings("deprecation")
	@Override
	public double getMaxHealth() {
		return entity.getMaxHealth();
		//		try {
		//			return entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		//		} catch (NoClassDefFoundError e) {
		//			return entity.getMaxHealth();
		//		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void launch(Class<? extends com.kmecpp.osmium.api.entity.Projectile> projectile) {
		entity.launchProjectile((Class<? extends Projectile>) projectile);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void launch(Class<? extends com.kmecpp.osmium.api.entity.Projectile> projectile, Direction direction) {
		entity.launchProjectile((Class<? extends Projectile>) projectile, direction.toBukkitVector());
	}

}
