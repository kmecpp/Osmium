package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Material;

import com.kmecpp.osmium.api.inventory.ItemStack;

public class BukkitItemStack implements ItemStack {

	private org.bukkit.inventory.ItemStack itemStack;

	public BukkitItemStack(org.bukkit.inventory.ItemStack itemStack) {
		this.itemStack = itemStack;
		if (itemStack == null) {
			this.itemStack = new org.bukkit.inventory.ItemStack(Material.AIR);
		}
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
