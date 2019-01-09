package com.kmecpp.osmium.platform.bukkit;

import java.util.Collection;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Wrappers;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.util.Reflection;

public class BukkitInventory implements Inventory {

	private static final long serialVersionUID = 6821956138573778155L;

	private org.bukkit.inventory.Inventory inventory;

	public BukkitInventory(org.bukkit.inventory.Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public org.bukkit.inventory.Inventory getSource() {
		return inventory;
	}

	@Override
	public String getName() {
		return inventory.getName();
	}

	@Override
	public int getSize() {
		return inventory.getSize();
	}

	@Override
	public int getItemCount() {
		return inventory.getContents().length;
	}

	@Override
	public ItemStack getItem(int index) {
		return BukkitAccess.getItemStack(inventory.getItem(index));
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		inventory.setItem(index, Reflection.cast(itemStack.getSource()));
	}

	@Override
	public Collection<ItemStack> getItems() {
		return Wrappers.convert(inventory.getContents(), BukkitAccess::getItemStack);
	}

}
