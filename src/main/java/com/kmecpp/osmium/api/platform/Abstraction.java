package com.kmecpp.osmium.api.platform;

public interface Abstraction {

	void getSpongeSource();

	void getBukkitSource();

	public static Abstraction fromSource() {
		return null;
	}

}
