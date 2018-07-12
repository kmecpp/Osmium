package com.kmecpp.osmium.api.inventory;

import java.io.Serializable;

import com.kmecpp.osmium.api.Abstraction;

public interface Inventory extends Abstraction, Serializable {

	String getName();

	int getSize();

}
