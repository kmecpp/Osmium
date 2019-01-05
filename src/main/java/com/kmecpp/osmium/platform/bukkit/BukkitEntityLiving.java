package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.entity.LivingEntity;

import com.kmecpp.osmium.api.entity.EntityLiving;

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

}
