package com.kmecpp.osmium.api.inventory;

import com.kmecpp.osmium.api.Abstraction;

public interface ItemStack extends Abstraction {

	//	String getId();

	String getDisplayName();

	int getAmount();

	boolean isEmpty();

}
