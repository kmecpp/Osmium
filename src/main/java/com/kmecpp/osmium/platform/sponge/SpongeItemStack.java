package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.item.ItemTypes;

import com.kmecpp.osmium.api.inventory.ItemStack;

public class SpongeItemStack implements ItemStack {

	private org.spongepowered.api.item.inventory.ItemStack itemStack;

	public SpongeItemStack(org.spongepowered.api.item.inventory.ItemStack itemStack) {
		this.itemStack = itemStack;
		if (itemStack == null) {
			this.itemStack = org.spongepowered.api.item.inventory.ItemStack.builder().itemType(ItemTypes.AIR).build();
		}
	}

	@Override
	public org.spongepowered.api.item.inventory.ItemStack getSource() {
		return itemStack;
	}

	@Override
	public String getDisplayName() {
		return itemStack.get(DisplayNameData.class).get().displayName().get().toString();
	}

	@Override
	public int getAmount() {
		return itemStack.getQuantity();
	}

	@Override
	public boolean isEmpty() {
		return itemStack.getType() == ItemTypes.AIR;
	}

}
