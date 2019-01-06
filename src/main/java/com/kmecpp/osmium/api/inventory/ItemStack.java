package com.kmecpp.osmium.api.inventory;

import com.kmecpp.osmium.api.Abstraction;

public interface ItemStack extends Abstraction {

	//	private ItemType type;
	//	private int amount;
	//
	//	public ItemType getType() {
	//		return type;
	//	}
	//
	//	public int getAmount() {
	//		return amount;
	//	}
	//
	//	public boolean isEmpty() {
	//		return amount == 0;
	//	}

	//	String getId();
	//
	ItemType getType();

	String getDisplayName();

	int getAmount();

	boolean isEmpty();

}
