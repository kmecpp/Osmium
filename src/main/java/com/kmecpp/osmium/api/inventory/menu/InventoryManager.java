package com.kmecpp.osmium.api.inventory.menu;

import java.util.HashMap;
import java.util.UUID;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.event.events.ItemDropEvent;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.inventory.ClickType;

public class InventoryManager {

	private static HashMap<UUID, HotbarMenu> hotbarMenus = new HashMap<>();
	private static HashMap<UUID, InventoryMenu> inventoryMenus = new HashMap<>();

	@Listener
	public void on(PlayerInteractEvent.Item e) {
		Player player = e.getPlayer();
		HotbarMenu menu = hotbarMenus.get(player.getUniqueId());
		if (menu == null || e.getClickType().isLeft()) {
			return;
		}

		MenuItem item = menu.getItems()[player.getSelectedSlot()];
		if (item != null && item.getClickHandler() != null && item.getItemStack().equals(player.getItemInMainHand())) {
			item.getClickHandler().onClick(new ClickEvent(player, e.getClickType(), item));
			e.setCancelled(true);
		}
	}

	@Listener
	public void on(InventoryEvent.Click e) {
		Player player = e.getPlayer();
		InventoryMenu menu = inventoryMenus.get(player.getUniqueId());
		if (menu == null || e.getSlot() >= menu.getItems().length || e.getSlot() < 0) {
			return;
		}

		MenuItem item = menu.getItems()[e.getSlot()];
		if (item == null) {
			return;
		}

		if (item.getClickHandler() != null) {
			item.getClickHandler().onClick(new ClickEvent(player, ClickType.LEFT, item));
			//			player.closeInventory();
		} else {
			//TODO: Play sound
		}
		e.setCancelled(true);
	}

	@Listener
	public void on(ItemDropEvent.Player e) {
		if (hotbarMenus.containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@Listener
	public void on(InventoryEvent.Close e) {
		inventoryMenus.remove(e.getPlayer().getUniqueId());
	}

	public static void openInventoryMenu(Player player, InventoryMenu menu) {
		inventoryMenus.put(player.getUniqueId(), menu);
	}

	public static boolean closeInventoryMenu(Player player) {
		player.closeInventory();
		return inventoryMenus.remove(player.getUniqueId()) != null;
	}

	public static void openHotbarMenu(Player player, HotbarMenu menu) {
		hotbarMenus.put(player.getUniqueId(), menu);
	}

	public static boolean closeHotbarMenu(Player player) {
		player.getInventory().clear();
		return hotbarMenus.remove(player.getUniqueId()) != null;
	}

}
