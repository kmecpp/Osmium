package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.inventory.Inventory;

public class BukkitInventory implements Inventory {
	
	private static final long serialVersionUID = 6821956138573778155L;
	
	private org.bukkit.inventory.Inventory inventory;

	public BukkitInventory(org.bukkit.inventory.Inventory inventory) {
		this.inventory = inventory;
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
	public org.bukkit.inventory.Inventory getSource() {
		return inventory;
	}

}
