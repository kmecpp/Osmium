package com.kmecpp.osmium.api.inventory.menu;

import org.bukkit.Material;
import org.spongepowered.api.item.ItemTypes;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;

public enum Resource {

	IRON(Chat.WHITE, "Iron", ItemStack.of(ItemType.IRON_INGOT)),
	GOLD(Chat.GOLD, "Gold", ItemStack.of(ItemType.GOLD_INGOT)),
	DIAMOND(Chat.AQUA, "Diamond", ItemStack.of(ItemType.DIAMOND)),
	EMERALD(Chat.GREEN, "Emerald", ItemStack.of(ItemType.EMERALD)),

	;

	private String name;
	private Chat color;
	private ItemStack example;

	private Resource(Chat color, String name, ItemStack example) {
		this.name = name;
		this.color = color;
		this.example = example;
	}

	public Chat getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public ItemStack getExample() {
		return example;
	}

	public static Resource fromType(ItemType type) {
		if (type == ItemType.IRON_INGOT) {
			return IRON;
		} else if (type == ItemType.GOLD_INGOT) {
			return GOLD;
		} else if (type == ItemType.DIAMOND) {
			return DIAMOND;
		} else if (type == ItemType.EMERALD) {
			return EMERALD;
		}
		return null;
	}

	public static Resource fromSourceType(Object type) {
		if (Platform.isBukkit()) {
			if (type == Material.IRON_INGOT) {
				return IRON;
			} else if (type == Material.GOLD_INGOT) {
				return GOLD;
			} else if (type == Material.DIAMOND) {
				return DIAMOND;
			} else if (type == Material.EMERALD) {
				return EMERALD;
			}
		} else if (Platform.isSponge()) {
			if (type == ItemTypes.IRON_INGOT) {
				return IRON;
			} else if (type == ItemTypes.GOLD_INGOT) {
				return GOLD;
			} else if (type == ItemTypes.DIAMOND) {
				return DIAMOND;
			} else if (type == ItemTypes.EMERALD) {
				return EMERALD;
			}
		}
		return null;
	}

}
