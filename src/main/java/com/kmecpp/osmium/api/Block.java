package com.kmecpp.osmium.api;

import com.kmecpp.osmium.Location;

public interface Block extends Abstraction {

	Location getLocation();

	int getX();

	int getY();

	int getZ();

}
