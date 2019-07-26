package com.kmecpp.osmium.api.inventory.menu;

import com.kmecpp.osmium.api.entity.Player;

@FunctionalInterface
public interface PlayerMenuBuilder {

	void build(Player player, InventoryMenu menu);

}
