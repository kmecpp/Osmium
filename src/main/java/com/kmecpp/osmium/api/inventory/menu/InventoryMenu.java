package com.kmecpp.osmium.api.inventory.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.InventoryType;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.core.OsmiumCore;

public class InventoryMenu extends AbstractInventoryMenu<InventoryMenu> {

	//	private HashSet<Player> viewers = new HashSet<>();
	private Inventory inventory;

	public InventoryMenu(String title) {
		this(title, InventoryType.CHEST);
	}

	public InventoryMenu(String title, InventoryType type) {
		title = Chat.style(title);
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

	public Inventory getInventory() {
		return inventory;
	}

	@Listener
	public void on(InventoryEvent.Interact baseEvent) {
		System.out.println("CLICK!");
		if (baseEvent.getInventory().getSource() == inventory.getSource()) {
			baseEvent.setCancelled(true);

			if (baseEvent instanceof InventoryEvent.Click) {
				InventoryEvent.Click e = (InventoryEvent.Click) baseEvent;

				MenuItem item = items[e.getSlot()];
				if (item != null && item.getClickHandler() != null) {
					item.getClickHandler().onClick(new ClickEvent(e.getPlayer(), e.getClick(), item));
				}
			}
		}
	}

	@Listener
	public void on(PlayerInteractEvent.Item e) {
		System.out.println("Interact with Item!!");
		//			InventoryMenu menu = new InventoryMenu("Test", InventoryType.DOUBLE_CHEST);
		//			menu.set(0, MenuItem.of(ItemType.DIAMOND_BLOCK, click -> System.out.println("Clicked!")));
		//			menu.send(e.getPlayer());
	}

	@Listener
	public void on(InventoryEvent.Close e) {
		//			viewers.remove(e.getPlayer());
		System.out.println("CLOSED!");
	}

	@Override
	public InventoryMenu set(int index, MenuItem item) {
		super.set(index, item);
		inventory.setItem(index, item.getItemStack());
		return this;
	}

	@Override
	public InventoryMenu set(int row, int col, MenuItem item) {
		super.set(row, col, item);
		inventory.setItem(9 * row + col, item.getItemStack());
		return this;
	}

	@Override
	public void send(Player player) {
		player.openInventory(inventory);
		InventoryManager.openInventoryMenu(player, this);
	}

	public void update() {
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				inventory.setItem(i, items[i].getItemStack());
			}
		}
	}

	//	public HashSet<Player> getViewers() {
	//		return viewers;
	//	}

}
