package com.kmecpp.osmium.api.inventory.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.InventoryType;
import com.kmecpp.osmium.api.inventory.ItemType;
import com.kmecpp.osmium.api.inventory.menu.MenuItem.ItemCost;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.core.OsmiumCore;
import com.kmecpp.osmium.platform.BukkitAccess;
import com.kmecpp.osmium.platform.SpongeAccess;

public class InventoryMenu extends AbstractInventoryMenu<InventoryMenu> {

	//	private HashSet<Player> viewers = new HashSet<>();
	private InventoryType inventoryType;
	PlayerMenuBuilder builder;
	//	private MenuItem[][] tabs;

	MenuItem[][] tabs;
	//	MenuBuilder[] tabs;
	//	boolean buildingTab;
	//	boolean currentTabItems[];
	int currentTab;

	private static final MenuItem ENABLED = MenuItem.of(ItemType.STAINED_GLASS_PANE, 5);
	private static final MenuItem DISABLED = MenuItem.of(ItemType.STAINED_GLASS_PANE, 15);

	//	public InventoryMenu(String title) {
	//		this(title, InventoryType.CHEST, null);
	//	}
	//
	//	public InventoryMenu(String title, InventoryType type) {
	//		this(title, type, null);
	//	}
	//
	//	public InventoryMenu(String title, MenuBuilder builder) {
	//		this(title, InventoryType.CHEST, builder);
	//	}

	private InventoryMenu(String title, InventoryType type, int size) {
		super(size);
		title = Chat.style(title);
		if (Platform.isBukkit()) {
			org.bukkit.inventory.Inventory bukkitInventory = Bukkit.createInventory(new InventoryHolder() {

				@Override
				public org.bukkit.inventory.Inventory getInventory() {
					return (org.bukkit.inventory.Inventory) inventory.getSource();
				}

			}, size, title); //(org.bukkit.event.inventory.InventoryType) type.getSource()
			this.inventory = BukkitAccess.getInventory(bukkitInventory);
		} else {
			org.spongepowered.api.item.inventory.Inventory spongeInventory = org.spongepowered.api.item.inventory.Inventory.builder()
					.of((InventoryArchetype) type.getSource())
					.build(Osmium.getPlugin(Reflection.getInvokingClass()));
			this.inventory = SpongeAccess.getInventory(spongeInventory);
		}
		//		this.items = new MenuItem[inventory.getSize()];
	}

	private InventoryMenu(String title, Object sourceInventory, int size) {
		super(size);
		title = Chat.style(title);
		if (Platform.isBukkit()) {
			this.inventory = BukkitAccess.getInventory((org.bukkit.inventory.Inventory) sourceInventory);
		} else {
			this.inventory = SpongeAccess.getInventory((org.spongepowered.api.item.inventory.Inventory) sourceInventory);
		}
	}

	private InventoryMenu() {
	}

	static {
		OsmiumCore.getPlugin().provideInstance(new InventoryMenu());
	}

	public static InventoryMenu small(String name) {
		return new InventoryMenu(name, InventoryType.CHEST, 9 * 3);
	}

	public static InventoryMenu large(String name) {
		return new InventoryMenu(name, InventoryType.CHEST, 9 * 6);
	}

	//	@Override
	//	public InventoryMenu set(int index, MenuItem item) {
	//		super.set(index, item);
	//		if (buildingTab) {
	//			if (currentTabItems == null) {
	//				currentTabItems = new boolean[items.length];
	//			}
	//			currentTabItems[index - 1] = true;
	//		}
	//		return this;
	//	}

	public InventoryMenu setTab(int index, MenuItem icon, MenuBuilder builder) {
		if (tabs == null) {
			//			tabs = new MenuBuilder[9];
			for (int i = 10; i < 19; i++) {
				set(i, DISABLED);
			}
			tabs = new MenuItem[9][items.length];
		}
		InventoryMenu menu = new InventoryMenu(inventory.getName(), inventoryType, inventory.getSize());
		builder.build(menu);
		tabs[index - 1] = menu.getItems();
		//		tabs[index - 1][index - 1] = icon;
		set(index, icon);
		//		tabs[index - 1] = builder;
		//		set(index, icon);
		return this;
		//		MenuItem[] tab = tabs[index];
		//		for (int i = 0; i < menu.items.length; i++) {
		//			MenuItem item = menu.items[i];
		//			if (item != null) {
		//				tab[i] = item;
		//			}
		//		}
		//		return this;
	}

	public void selectTab(Player player, int index) {
		long start = System.nanoTime();
		//		if (currentTab == index) {
		//			return;
		//		}

		//		EnumMap<Resource, Integer> resources = new EnumMap<>(Resource.class);
		int[] resources = new int[Resource.values().length];

		if (Platform.isBukkit()) {
			for (org.bukkit.inventory.ItemStack item : ((org.bukkit.entity.Player) player.getSource()).getInventory().getContents()) {
				if (item == null) {
					continue;
				}

				Resource resource = Resource.fromSourceType(item.getType());
				if (resource != null) {
					int existing = resources[resource.ordinal()];
					resources[resource.ordinal()] = existing + item.getAmount();
				}
			}
		}

		//		for (ItemStack item : player.getInventory().getItems()) {
		//			if (item == null) {
		//				continue;
		//			}
		//
		//			Resource resource = Resource.fromType(item.getType());
		//			if (resource != null) {
		//				int existing = resources[resource.ordinal()];
		//				resources[resource.ordinal()] = existing + item.getAmount();
		//			}
		//			System.out.println(item.getType());
		//		}
		//		System.out.println("RESOURCES: " + resources);

		//Version 3
		MenuItem[] tab = tabs[index - 1];
		if (tab == null) {
			return;
		}

		if (currentTab != 0) {
			set(9 + currentTab, DISABLED);
		}
		set(9 + index, ENABLED);

		boolean[] replaced = new boolean[tab.length];
		for (int i = 0; i < tab.length; i++) {
			MenuItem item = tab[i];
			if (item == null) {
				continue;
			}
			ItemCost cost = item.getCost();
			if (cost != null) {
				if (resources[cost.getType().ordinal()] >= cost.getAmount()) {
					cost.update(item.getItemStack(), true);
					//					item.setName(Chat.GREEN + Chat.strip(item.getName()));
					//					item.setLastDescriptionLine(Chat.GREEN + "Click to purchase this item!");
				} else {
					cost.update(item.getItemStack(), false);
					//					item.setName(Chat.RED + item.getName());
					//					item.setLastDescriptionLine(Chat.RED + "You do not have enough " + cost.getType().getName() + "!");
				}
			}

			set(i + 1, item);
			replaced[i] = true;
		}

		if (currentTab != 0) {
			MenuItem[] lastTab = tabs[currentTab - 1];
			for (int i = 0; i < lastTab.length; i++) {
				if (!replaced[i] && lastTab[i] != null) {
					remove(i + 1);
				}
			}
		}

		currentTab = index;

		System.out.println("TOTAL TIME: " + (System.nanoTime() - start) / 1000F + "us");

		//Version 2
		//		MenuBuilder tab = tabs[index - 1];
		//		if (tab != null) {
		//			if (currentTab != 0) {
		//				set(9 + currentTab, DISABLED);
		//			}
		//			set(9 + index, ENABLED);
		//
		//			if (currentTabItems != null) {
		//				for (int i = 0; i < currentTabItems.length; i++) {
		//					if (currentTabItems[i]) {
		//						remove(i);
		//					}
		//				}
		//			}
		//			buildingTab = true;
		//			tab.build(player, this);
		//			buildingTab = false;
		//			this.currentTab = index;
		//		}

		//Version 1
		//		MenuItem[] newTab = tabs[index];
		//		boolean[] replaced = new boolean[items.length];
		//		for (int i = 0; i < newTab.length; i++) {
		//			MenuItem item = newTab[i];
		//			if (item != null) {
		//				replaced[i] = true;
		//			}
		//		}
		//
		//		MenuItem[] lastTab = tabs[currentTab];
		//		for (int i = 0; i < replaced.length; i++) {
		//			if (lastTab[i] != null && !replaced[i]) {
		//				remove(i);
		//			}
		//		}
		//		currentTab = index;
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
					item.getClickHandler().onClick(new ClickEvent(e.getPlayer(), e.getClick(), this, item));
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

	public InventoryMenu setBuilder(PlayerMenuBuilder builder) {
		this.builder = builder;
		return this;
	}

	@Override
	public void send(Player player) {
		if (builder != null) {
			builder.build(player, this);
		}
		if (tabs != null) {
			selectTab(player, 1);
		}

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
