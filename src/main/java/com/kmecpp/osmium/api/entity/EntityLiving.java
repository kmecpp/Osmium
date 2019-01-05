package com.kmecpp.osmium.api.entity;

public interface EntityLiving extends Entity {

	double getHealth();

	void setHealth(double health);

	double getMaxHealth();

	default void kill() {
		setHealth(0);
	}

	default void heal() {
		setHealth(getMaxHealth());
	}

}
