package com.kmecpp.osmium.api.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.core.OsmiumCore;

public class InventoryMenu {

	//	private HashSet<Player> viewers = new HashSet<>();
	private Inventory inventory;
	private MenuItem[] items;

	public InventoryMenu(String title, InventoryType type) {
		if (Platform.isBukkit()) {
			org.bukkit.inventory.Inventory bukkitInventory = Bukkit.createInventory(new InventoryHolder() {

				@Override
				public org.bukkit.inventory.Inventory getInventory() {
					return (org.bukkit.inventory.Inventory) inventory.getSource();
				}

			}, (org.bukkit.event.inventory.InventoryType) type.getSource(), title);
			this.inventory = BukkitAccess.getInventory(bukkitInventory);
		} else {
			org.spongepowered.api.item.inventory.Inventory spongeInventory = org.spongepowered.api.item.inventory.Inventory.builder()
					.of((InventoryArchetype) type.getSource())
					.build(Osmium.getPlugin(Reflection.getInvokingClass()));
			this.inventory = SpongeAccess.getInventory(spongeInventory);
		}
		this.items = new MenuItem[inventory.getSize()];
	}

	private InventoryMenu() {
	}

	static {
		OsmiumCore.getPlugin().provideInstance(new InventoryMenu());
	}

	@Listener
	public void on(InventoryEvent.Interact baseEvent) {
		if (baseEvent.getInventory().getSource() == inventory.getSource()) {
			baseEvent.setCancelled(true);

			if (baseEvent instanceof InventoryEvent.Click) {
				InventoryEvent.Click e = (InventoryEvent.Click) baseEvent;

				MenuItem item = items[e.getSlot()];
				if (item != null && item.clickHandler != null) {
					item.clickHandler.onClick(new ClickEvent(e.getPlayer(), e.getClick(), item));
				}
			}
		}
	}

	public static class InventoryListener {

		@Listener
		public void on(PlayerInteractEvent.Item e) {
			System.out.println("Interact with Item!!");
			InventoryMenu menu = new InventoryMenu("Test", InventoryType.DOUBLE_CHEST);
			menu.set(0, MenuItem.of(ItemType.DIAMOND_BLOCK, click -> System.out.println("Clicked!")));
			menu.send(e.getPlayer());
		}

		@Listener
		public void on(InventoryEvent.Close e) {
			//			viewers.remove(e.getPlayer());
			System.out.println("CLOSED!");
		}

	}

	public void set(int index, MenuItem item) {
		inventory.setItem(index, item.itemStack);
		items[index] = item;
	}

	public void send(Player player) {
		//		this.viewers.add(player);
		player.openInventory(inventory);
	}

	//	public HashSet<Player> getViewers() {
	//		return viewers;
	//	}

	public static class MenuItem {

		private ItemStack itemStack;
		private ClickHandler clickHandler;

		private MenuItem(ItemStack itemStack, ClickHandler clickHandler) {
			this.itemStack = itemStack;
			this.clickHandler = clickHandler;
		}

		public static MenuItem of(ItemType type) {
			return of(type, null);
		}

		public static MenuItem of(ItemType type, ClickHandler clickHandler) {
			return of(null, type, clickHandler);
		}

		public static MenuItem of(String name, ItemType type) {
			return of(name, type, null);
		}

		public static MenuItem of(String name, ItemType type, ClickHandler clickHandler) {
			ItemStack.Builder builder = ItemStack.builder();
			builder.type(type);
			if (name != null) {
				builder.name(name);
			}
			return new MenuItem(builder.build(), clickHandler);
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public ClickHandler getClickHandler() {
			return clickHandler;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {

			private MenuItem item = new MenuItem(ItemStack.of(ItemType.AIR), null);

			public void name(String name) {
				item.itemStack.setDisplayName(name);
			}

			public void type(ItemType type) {
				item.itemStack.setType(type);
			}

			public void item(ItemStack itemStack) {
				item.itemStack = itemStack;
			}

			public void handler(ClickHandler e) {
				item.clickHandler = e;
			}

		}

	}

	public static interface ClickHandler {

		void onClick(ClickEvent e);

	}

	public static class ClickEvent {

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

		public MenuItem getClickedItem() {
			return clickedItem;
		}

	}

}
