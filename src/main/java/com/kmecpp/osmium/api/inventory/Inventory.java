package com.kmecpp.osmium.api.inventory;

import java.io.Serializable;
import java.util.Collection;

import com.kmecpp.osmium.api.Abstraction;

public interface Inventory extends Abstraction, Serializable {

	String getName();

	int getSize();

	int getItemCount();

	ItemStack getItem(int index);

	void setItem(int index, ItemStack itemStack);

	Collection<ItemStack> getItems();

	default boolean isEmpty() {
		return getItemCount() == 0;
	}

}
