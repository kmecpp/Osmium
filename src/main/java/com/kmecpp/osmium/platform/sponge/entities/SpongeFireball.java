package com.kmecpp.osmium.platform.sponge.entities;

import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;

import com.kmecpp.osmium.api.Projectile;

public class SpongeFireball implements Projectile {

	private Fireball fireball;

	public SpongeFireball(Fireball fireball) {
		this.fireball = fireball;
	}

	@Override
	public Fireball getSource() {
		return fireball;
	}

}
