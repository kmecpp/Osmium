package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Material;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;

public class BukkitItemStack implements ItemStack {

	private ItemType type;
	private org.bukkit.inventory.ItemStack itemStack;

	public BukkitItemStack(org.bukkit.inventory.ItemStack itemStack) {
		this.itemStack = itemStack != null ? itemStack : new org.bukkit.inventory.ItemStack(Material.AIR);
		this.type = BukkitAccess.getItemType(itemStack);
	}

	@Override
	public ItemType getType() {
		return type;
	}

	@Override
	public org.bukkit.inventory.ItemStack getSource() {
		return itemStack;
	}

	@Override
	public String getDisplayName() {
		return itemStack.getItemMeta().getDisplayName();
	}

	@Override
	public int getAmount() {
		return itemStack.getAmount();
	}

	@Override
	public boolean isEmpty() {
		return itemStack.getType() == Material.AIR;
	}

}
