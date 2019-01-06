package com.kmecpp.osmium.api.inventory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.bukkit.Material;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemTypes;

import com.kmecpp.osmium.api.platform.Platform;

public class ItemManager {

	private HashMap<String, MaterialType> itemTypes = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T extends MaterialType> T getItemType(String id) {
		return (T) itemTypes.get(id);
	}

	<T extends MaterialType> void register(T[] types, boolean blocks) {
		Field sourceField;
		try {
			sourceField = types.getClass().getComponentType().getDeclaredField("source");
			sourceField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new Error(e); //Should never happen. TODO: write test
		}

		for (T type : types) {
			try {
				if (!Platform.isBukkit()) {
					for (Material material : Material.values()) {
						if (material.name().equals(type.name())) {
							sourceField.set(type, material);
							itemTypes.put(material.toString(), type);
							break;
						}
					}
				} else if (Platform.isSponge()) {
					for (Field field : blocks ? BlockTypes.class.getFields() : ItemTypes.class.getFields()) {
						if (!Modifier.isStatic(field.getModifiers()) || field.getType() != (blocks ? org.spongepowered.api.block.BlockType.class : org.spongepowered.api.item.ItemType.class)) {
							continue;
						}
						if (field.getName().equals(type.name())) {
							try {
								CatalogType spongeCatalogedItem = (CatalogType) field.get(null);
								sourceField.set(type, spongeCatalogedItem);
								itemTypes.put(spongeCatalogedItem.getKey().getValue(), type);
								break;
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}
