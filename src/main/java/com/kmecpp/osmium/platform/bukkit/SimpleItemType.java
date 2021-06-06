package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kmecpp.osmium.api.persistence.Serialization;

public class SimpleItemType {

	private Material material;
	private int materialData;

	public SimpleItemType(Material material, int materialData) {
		this.material = material;
		this.materialData = materialData;
	}

	public static SimpleItemType fromItemStack(ItemStack itemStack) {
		return new SimpleItemType(itemStack.getType(), itemStack.getDurability());
	}

	public Material getMaterial() {
		return material;
	}

	public int getMaterialData() {
		return materialData;
	}

	@Override
	public String toString() {
		return material + (materialData == 0 ? "" : (":" + materialData));
	}

	public static SimpleItemType fromString(String str) {
		String[] parts = str.split(":", 2);
		Material material = Material.valueOf(parts[0].toUpperCase());
		int data = parts.length == 1 ? 0 : Integer.parseInt(parts[1]);
		return new SimpleItemType(material, data);
	}

	static {
		Serialization.register(SimpleItemType.class, SimpleItemType::fromString);
	}

}
