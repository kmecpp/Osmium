package com.kmecpp.osmium.api.inventory.menu;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;

public class HotbarMenu extends AbstractInventoryMenu<HotbarMenu> {

	public HotbarMenu() {
		super(9);
	}

	@Override
	public void send(Player player) {
		Inventory inventory = player.getInventory();
		for (int i = 0; i < items.length; i++) {
			MenuItem item = items[i];
			if (item != null) {
				inventory.setItem(i, items[i].getItemStack());
			}
		}
		InventoryManager.openHotbarMenu(player, this);
	}

}
