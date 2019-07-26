package com.kmecpp.osmium.platform.bukkit;

import java.util.Collection;

import org.bukkit.Material;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Wrappers;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.InventoryType;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;

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
	public boolean containsAtLeast(ItemStack itemStack, int amount) {
		return inventory.containsAtLeast((org.bukkit.inventory.ItemStack) itemStack.getSource(), amount);
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		inventory.setItem(index, itemStack == null ? null : (org.bukkit.inventory.ItemStack) itemStack.getSource());
	}

	@Override
	public void addItem(ItemStack itemStack) {
		inventory.addItem((org.bukkit.inventory.ItemStack) itemStack.getSource());
	}

	@Override
	public boolean take(ItemType type, int amount) {
		Material bukkitType = (Material) type.getSource();
		org.bukkit.inventory.ItemStack[] contents = inventory.getContents();
		for (int i = 0; i < contents.length; i++) {
			org.bukkit.inventory.ItemStack itemStack = contents[i];
			if (itemStack != null) {
				if (itemStack.getType() == bukkitType) {
					int take = Math.min(itemStack.getAmount(), amount);
					itemStack.setAmount(itemStack.getAmount() - take);
					inventory.setItem(i, itemStack);
					amount -= take;
					if (amount <= 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Collection<ItemStack> getItems() {
		return Wrappers.convert(inventory.getContents(), BukkitAccess::getItemStack);
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public InventoryType getType() {
		return InventoryType.fromSource(inventory.getType());
	}

}
