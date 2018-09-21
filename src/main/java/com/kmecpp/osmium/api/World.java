package com.kmecpp.osmium.api;

import java.util.UUID;

public interface World extends Abstraction {

	UUID getUniqueId();

	String getName();

	int getHighestYAt(int x, int z);

}
