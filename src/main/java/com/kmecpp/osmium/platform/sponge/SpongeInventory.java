package com.kmecpp.osmium.platform.sponge;

import com.kmecpp.osmium.api.inventory.Inventory;

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

}
