package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.item.ItemTypes;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;

public class SpongeItemStack implements ItemStack {

	private org.spongepowered.api.item.inventory.ItemStack itemStack;
	private ItemType type;

	public SpongeItemStack(org.spongepowered.api.item.inventory.ItemStack itemStack) {
		this.itemStack = itemStack != null ? itemStack : org.spongepowered.api.item.inventory.ItemStack.builder().itemType(ItemTypes.AIR).build();
		this.type = SpongeAccess.getItemType(itemStack);
	}

	@Override
	public ItemType getType() {
		return type;
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
