package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kmecpp.osmium.api.command.Chat;

public class BukkitUtil {

	private static ItemNameGetter itemNameGetter;

	public static void setItemName(ItemStack itemStack, String name) {
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(Chat.style(name));
		itemStack.setItemMeta(meta);
	}

	public static String getItemName(ItemStack itemStack) {
		return itemNameGetter == null ? itemStack.getType().name() : itemNameGetter.get(itemStack);
	}

	public static void setItemNameGetter(ItemNameGetter itemNameGetter) {
		BukkitUtil.itemNameGetter = itemNameGetter;
	}

	public static void setItemDescription(ItemStack itemStack, String description) {
		ItemMeta meta = itemStack.getItemMeta();
		meta.setLore(description == null ? null : Chat.styleLines(description));
		itemStack.setItemMeta(meta);
	}

	@FunctionalInterface
	public static interface ItemNameGetter {

		String get(ItemStack itemStack);

	}

}
