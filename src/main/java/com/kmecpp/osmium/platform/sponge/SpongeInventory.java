package com.kmecpp.osmium.platform.sponge;

import java.util.Collection;

import org.spongepowered.api.item.inventory.property.SlotIndex;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.Wrappers;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.util.Reflection;

public class SpongeInventory implements Inventory {

	private static final long serialVersionUID = -1376070678387588198L;

	private org.spongepowered.api.item.inventory.Inventory inventory;

	public SpongeInventory(org.spongepowered.api.item.inventory.Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public org.spongepowered.api.item.inventory.Inventory getSource() {
		return inventory;
	}

	@Override
	public String getName() {
		return inventory.getName().get();
	}

	@Override
	public int getSize() {
		return inventory.size();
	}

	@Override
	public int getItemCount() {
		return inventory.totalItems();
	}

	@Override
	public ItemStack getItem(int index) {
		return SpongeAccess.getItemStack(inventory.getSlot(SlotIndex.of(index)).orElseThrow(IndexOutOfBoundsException::new).peek());
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		inventory.getSlot(SlotIndex.of(index)).get().set(Reflection.cast(itemStack.getSource()));
	}

	@Override
	public Collection<ItemStack> getItems() {
		return Wrappers.convert(inventory.slots(), slot -> SpongeAccess.getItemStack(slot.peek()));
	}

}
