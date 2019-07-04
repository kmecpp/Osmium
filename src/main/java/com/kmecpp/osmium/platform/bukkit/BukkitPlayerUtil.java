package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import com.kmecpp.osmium.core.OsmiumCore;

/**
 * A utility class for handling players
 */
public final class BukkitPlayerUtil {

	private BukkitPlayerUtil() {
	}

	/**
	 * Gets the IP address of the given player
	 * 
	 * @param player
	 *            the player whose ip address to get
	 * @return the player's IP
	 */
	public static String getIp(Player player) {
		return player.getAddress().getAddress().getHostAddress();
	}

	/**
	 * Sets the player's XP from a float where the integer part represents the
	 * players current level, and the decimal part represents the percentage to
	 * the next level.
	 * 
	 * @param player
	 *            the player whose XP to set
	 * @param xp
	 *            the new XP for that player
	 */
	public static void setXp(Player player, float xp) {
		player.setTotalExperience((int) xp);
		player.setExp(xp - player.getTotalExperience());
	}

	/**
	 * Gets the players experience as a float. The integer part of the number
	 * represents the players current level, and the decimal part represents the
	 * percentage to the next level.
	 * 
	 * @param player
	 *            the player whose XP to get
	 * @return the player's XP
	 */
	public static float getXp(Player player) {
		return player.getTotalExperience() + player.getExp();
	}

	/**
	 * Gets the player's skull as an item stack with a size of one
	 *
	 * @param player
	 *            the player whose skull to get
	 * @return the player's skull
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getSkull(Player player) {
		SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		meta.setOwner(player.getName());
		ItemStack item = new ItemStack(Material.SKULL_ITEM);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Clears the players inventory, sets their XP to zero and removes all
	 * potion effects, effectively resetting the player to their default state
	 *
	 * @param player
	 *            the player to reset
	 */
	public static void reset(Player player) {
		player.setLevel(0);
		player.setExp(0);
		heal(player);
		clearInventory(player);
	}

	//INVENTORY

	/**
	 * Sets the player's current inventory
	 *
	 * @param player
	 *            the player whose inventory to set
	 * @param inventory
	 *            the new inventory for that player
	 */
	public static void setInventory(Player player, Inventory inventory) {
		PlayerInventory playerInv = player.getInventory();
		for (int i = 0; i < inventory.getSize(); i++) {
			playerInv.setItem(i, inventory.getItem(i));
		}
	}

	/**
	 * Sets the player's armor contents to the specified ItemStack array, which
	 * must have a length of exactly four. The item at index 0 will be set to
	 * the players boots, index 1 will be their leggings and so on.
	 *
	 * @param player
	 *            the player whose armor to set
	 * @param armor
	 *            the ItemStack's to set the player's armor to
	 * @throws IllegalArgumentException
	 *             if the armor array does not have a length of four
	 */
	public static void setArmor(Player player, ItemStack[] armor) {
		player.getInventory().setBoots(armor[0]);
		player.getInventory().setChestplate(armor[2]);
		player.getInventory().setLeggings(armor[1]);
		player.getInventory().setHelmet(armor[3]);
	}

	/**
	 * Clears the player's inventory
	 *
	 * @param player
	 *            the player whose inventory to clear
	 */
	public static void clearInventory(Player player) {
		//		player.getInventory().setContents(null);
		//		player.getInventory().setArmorContents(null);

		//				Inventory inventory = player.getInventory();
		//				for (int i = 0; i < inventory.getSize(); i++) {
		//					inventory.setItem(i, null);
		//				}
		//				player.updateInventory();
		//
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
	}

	/**
	 * Removes any item that matches the given id and damage values from the
	 * player's inventory
	 *
	 * @param player
	 *            the player whose inventory to search
	 * @param id
	 *            the id of the item to remove
	 * @param damage
	 *            the damage value of the item to remove or -1 for any value
	 * @return true if one or more items where removed from the player's
	 *         inventory
	 */
	@SuppressWarnings("deprecation")
	public static boolean removeItem(Player player, int id, short damage) {
		boolean confiscated = false;
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item != null && item.getTypeId() == id && (item.getDurability() == damage || damage == -1)) {
				inventory.setItem(i, null);
				confiscated = true;
			}
		}
		return confiscated;
	}

	/**
	 * Checks if the given player has an item in their inventory that matches if
	 * the given item id and damage
	 *
	 * @param player
	 *            the player whose inventory to search
	 * @param id
	 *            the id of the item to search for
	 * @param damage
	 *            the damage value of the item to search for or -1 for any value
	 * @return true if the player has the given item and false if they do not
	 */
	@SuppressWarnings("deprecation")
	public static boolean hasItem(Player player, int id, short damage) {
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item != null && item.getTypeId() == id && (item.getDurability() == damage || damage == -1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether or not the player has items in their inventory
	 *
	 * @param player
	 *            the player
	 * @return true if the player has any items, false otherwise
	 */
	public static boolean hasItems(Player player) {
		for (int i = 0; i <= player.getInventory().getContents().length; i++) {
			if (player.getInventory().getItem(i) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets whether or not the player is wearing armor
	 *
	 * @param player
	 *            the player
	 * @return true if the player has any armor on, false otherwise
	 */
	public static boolean hasArmor(Player player) {
		return player.getInventory().getHelmet() != null
				|| player.getInventory().getChestplate() != null
				|| player.getInventory().getLeggings() != null
				|| player.getInventory().getBoots() != null;
	}

	//ACTIONS
	/**
	 * Heals a player by giving them full health, food, and removing all potion
	 * effects
	 *
	 * @param player
	 *            the player
	 */
	public static void heal(Player player) {
		player.setHealth(20);
		player.setFoodLevel(20);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	public static void spawn(Player player) {
		spawn(player, player.getWorld());
	}

	public static void spawn(Player player, World world) {
		player.teleport(world.getSpawnLocation().add(0.5, 0, 0.5));
	}

	/**
	 * Teleports the player to the given world with the location adjusted to be
	 * in the center of the block
	 * 
	 * @param player
	 *            the player to teleport
	 * @param world
	 *            the world to teleport to
	 */
	public static void teleport(Player player, World world) {
		teleport(player, world.getSpawnLocation().getBlock());
	}

	public static void teleport(Player player, Block block) {
		player.teleport(block.getLocation().add(0.5, 0.0, 0.5));
	}

	//DATA
	/**
	 * Checks if the given string could be a valid Minecraft username
	 *
	 * @param username
	 *            the username
	 * @return whether or not the username is valid
	 */
	public static boolean isUsernameValid(String username) {
		return username.matches("^[a-zA-Z0-9_]*$");
	}

	public static void playSound(Player player, Sound sound) {
		playSound(player, sound, 1, 1);
	}

	public static void playSound(Player player, Sound sound, double pitch) {
		playSound(player, sound, 1, pitch);
	}

	public static void playSound(Player player, Sound sound, int volume, double pitch) {
		player.playSound(player.getLocation(), sound, volume, (float) pitch);
	}

	public static void playSound(final Player player, String sounds) {
		for (String s : sounds.split(",")) {
			String[] parts = s.split("-");
			final String name = parts[0].toUpperCase();
			final int volume = Integer.parseInt(parts[1]);
			final double pitch = Double.parseDouble(parts[2]);
			long delay = Long.parseLong(parts[3]);

			Bukkit.getScheduler().runTaskLater(OsmiumCore.getPlugin().getPluginImplementation(), new Runnable() {

				@Override
				public void run() {
					playSound(player, Sound.valueOf(name), volume, pitch);
				}

			}, delay);
			//				Logger.warn("Attempted to play invalid sound: '" + sounds + "'! The correct format is: Sound-Volume-Pitch-Delay");
			//				e.printStackTrace();
		}
	}

}
