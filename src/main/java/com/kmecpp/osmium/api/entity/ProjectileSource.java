package com.kmecpp.osmium.api.entity;

import com.kmecpp.osmium.api.location.Direction;

public interface ProjectileSource {

	void launch(Class<? extends Projectile> projectile);

	void launch(Class<? extends Projectile> projectile, Direction direction);

}
