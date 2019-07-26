package com.kmecpp.osmium.api.inventory.menu;

import com.kmecpp.osmium.api.command.Messageable;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.ClickType;

public class ClickEvent implements Messageable {

	private final Player player;
	private final ClickType clickType;
	private final AbstractInventoryMenu<?> menu;
	private final MenuItem clickedItem;

	public ClickEvent(Player player, ClickType clickType, AbstractInventoryMenu<?> menu, MenuItem clickedItem) {
		this.player = player;
		this.clickType = clickType;
		this.menu = menu;
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

	public AbstractInventoryMenu<?> getMenu() {
		return menu;
	}

	public void close() {
		player.closeInventory();
	}

	@Override
	public void sendMessage(String message) {
		player.sendMessage(message);
	}

}
