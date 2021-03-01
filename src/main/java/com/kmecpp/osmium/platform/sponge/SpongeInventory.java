package com.kmecpp.osmium.platform.sponge;

import java.util.Collection;

import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.Wrappers;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.InventoryType;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;

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
		return SpongeAccess.getItemStack(inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(index))).first().first());
	}

	@Override
	public boolean containsAtLeast(ItemStack itemStack, int amount) {
		throw new UnsupportedOperationException(); //TODO
	}

	@Override
	public boolean take(ItemType type, int amount) {
		throw new UnsupportedOperationException(); //TODO
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(index))).first().set(itemStack == null ? null : (org.spongepowered.api.item.inventory.ItemStack) itemStack.getSource());
	}

	@Override
	public void addItem(ItemStack itemStack) {
		inventory.offer((org.spongepowered.api.item.inventory.ItemStack) itemStack.getSource());
	}

	@Override
	public Collection<ItemStack> getItems() {
		return Wrappers.convert(inventory.slots(), slot -> SpongeAccess.getItemStack(slot.first()));
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public InventoryType getType() {
		return InventoryType.fromSource(inventory.getArchetype());
	}

}
