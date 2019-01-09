package com.kmecpp.osmium.api.inventory;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.core.OsmiumCore;

public class InventoryMenu {

	private HashSet<Player> viewers = new HashSet<>();
	private Inventory inventory;

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
	}

	private InventoryMenu() {
	}

	static {
		OsmiumCore.getPlugin().provideInstance(new InventoryMenu());
	}

	@Listener
	public void on(InventoryEvent.Click e) {
		if (e.getInventory().getSource() == inventory.getSource()) {
			processClick(e.getPlayer(), e.getSlot());
			e.setCancelled(true);
		}
	}

	@Listener
	public void on(InventoryEvent.Close e) {
		viewers.remove(e.getPlayer());
		System.out.println("CLOSED!");
	}

	public void processClick(Player player, int slot) {

	}

	public void set(int index) {
	}

	public void send(Player player) {
		this.viewers.add(player);
		player.openInventory(inventory);
	}

	public static class MenuItem {

		private ItemStack itemStack;

		public ItemStack getItemStack() {
			return itemStack;
		}

	}

}
