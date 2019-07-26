package com.kmecpp.osmium.platform.sponge.entities;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Villager;

import com.kmecpp.osmium.api.entity.entities.EntityVillager;
import com.kmecpp.osmium.platform.sponge.SpongeEntityLiving;

public class SpongeVillager extends SpongeEntityLiving implements EntityVillager {

	private Villager villager;

	public SpongeVillager(Villager villager) {
		super(villager);
	}

	@Override
	public double getHealth() {
		return villager.health().get();
	}

	@Override
	public void setHealth(double health) {
		villager.health().set(health);
	}

	@Override
	public double getMaxHealth() {
		return villager.get(Keys.MAX_HEALTH).get();
	}

}
