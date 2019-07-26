package com.kmecpp.osmium.api.inventory;

import java.io.Serializable;
import java.util.Collection;

import com.kmecpp.osmium.api.Abstraction;

public interface Inventory extends Abstraction, Serializable {

	String getName();

	InventoryType getType();

	int getSize();

	int getItemCount();

	ItemStack getItem(int index);

	void setItem(int index, ItemStack itemStack);

	boolean containsAtLeast(ItemStack itemStack, int amount);

	void addItem(ItemStack itemStack);

	boolean take(ItemType type, int amount);

	Collection<ItemStack> getItems();

	void clear();

	default boolean isEmpty() {
		return getItemCount() == 0;
	}

}
