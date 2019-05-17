package com.kmecpp.osmium.api.inventory.menu;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.ClickType;

public class ClickEvent {

	private final Player player;
	private final ClickType clickType;
	private final MenuItem clickedItem;

	public ClickEvent(Player player, ClickType clickType, MenuItem clickedItem) {
		this.player = player;
		this.clickType = clickType;
		this.clickedItem = clickedItem;
	}

	public Player getPlayer() {
		return player;
	}

	public ClickType getClickType() {
		return clickType;
	}

	public MenuItem getItem() {
		return clickedItem;
	}

	public void close() {
		player.closeInventory();
	}

}
