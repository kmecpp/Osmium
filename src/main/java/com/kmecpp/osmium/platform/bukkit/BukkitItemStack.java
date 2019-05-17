package com.kmecpp.osmium.platform.bukkit;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;
import com.kmecpp.osmium.api.util.Reflection;

public class BukkitItemStack implements ItemStack {

	private ItemType type;
	private org.bukkit.inventory.ItemStack itemStack;

	public BukkitItemStack(org.bukkit.inventory.ItemStack itemStack) {
		this.itemStack = itemStack != null ? itemStack : new org.bukkit.inventory.ItemStack(Material.AIR);
		this.type = BukkitAccess.getItemType(itemStack);
	}

	@Override
	public org.bukkit.inventory.ItemStack getSource() {
		return itemStack;
	}

	@Override
	public ItemType getType() {
		return type;
	}

	@Override
	public void setType(ItemType type) {
		itemStack.setType(Reflection.cast(type.getSource()));
	}

	@Override
	public int getDamage() {
		return itemStack.getDurability();
		//		if (itemStack.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
		//			return ((org.bukkit.inventory.meta.Damageable) itemStack.getItemMeta()).getDamage();
		//		}
		//		return 0;
	}

	@Override
	public void setDamage(int damage) {
		//		if (itemStack.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
		//			ItemMeta meta = itemStack.getItemMeta();
		//			((org.bukkit.inventory.meta.Damageable) meta).setDamage(damage);
		//			itemStack.setItemMeta(meta);
		//		}
		itemStack.setDurability((short) damage);
	}

	@Override
	public String getDisplayName() {
		return itemStack.getItemMeta().getDisplayName();
	}

	@Override
	public void setDisplayName(String name) {
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name);
		itemStack.setItemMeta(meta);
	}

	@Override
	public void setDescription(String description) {
		ItemMeta meta = itemStack.getItemMeta();
		meta.setLore(description == null ? null : Chat.styleLines(description));
		itemStack.setItemMeta(meta);
	}

	@Override
	public List<String> getDescription() {
		return itemStack.getItemMeta().getLore();
	}

	@Override
	public int getAmount() {
		return itemStack.getAmount();
	}

	@Override
	public void setAmount(int amount) {
		itemStack.setAmount(amount);
	}

}
