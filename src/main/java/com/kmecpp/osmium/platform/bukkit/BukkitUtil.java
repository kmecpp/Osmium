package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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

	public static String getNMSVersion() {
		String v = Bukkit.getServer().getClass().getPackage().getName();
		return v.substring(v.lastIndexOf('.') + 1);
	}

	public static void teleportWithoutChangingDirection(Entity entity, Location location) { //"player.teleport(new Location" should never be used unless trying to reset direction
		Location currentLocation = entity.getLocation();
		entity.teleport(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), currentLocation.getYaw(), currentLocation.getPitch()));
	}

	public static void teleportWithoutChangingDirection(Entity entity, World world, double x, double y, double z) { //"player.teleport(new Location" should never be used unless trying to reset direction
		Location currentLocation = entity.getLocation();
		entity.teleport(new Location(world, x, y, z, currentLocation.getYaw(), currentLocation.getPitch()));
	}

	@FunctionalInterface
	public static interface ItemNameGetter {

		String get(ItemStack itemStack);

	}

}
