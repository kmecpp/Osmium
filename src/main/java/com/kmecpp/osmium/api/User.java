package com.kmecpp.osmium.api;

import java.util.UUID;

public interface User extends Abstraction {

	UUID getUniqueId();

	String getName();

	boolean isOp();

	long getLastPlayed();

	long getFirstPlayed();

	boolean isOnline();

}
