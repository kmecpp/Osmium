package com.kmecpp.osmium.api.inventory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.HashMap;

import org.bukkit.Material;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemTypes;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class ItemManager {

	private EnumMap<Material, MaterialType> bukkitItemTypes;
	private HashMap<String, MaterialType> spongeItemTypes;

	@SuppressWarnings("unchecked")
	public <T extends MaterialType> T getItemType(Object source) {
		if (Platform.isBukkit()) {
			return (T) bukkitItemTypes.get(source);
		} else if (Platform.isSponge()) {
			return (T) spongeItemTypes.get(((org.spongepowered.api.item.ItemType) source).getKey().getValue());
		}
		return null;
		//		return (T) spongeItemTypes.get(id);
	}

	<T extends MaterialType> void register(T[] types, boolean blocks) {
		if (Platform.isBukkit() && bukkitItemTypes == null) {
			bukkitItemTypes = new EnumMap<>(Material.class);
		} else if (Platform.isSponge() && spongeItemTypes == null) {
			spongeItemTypes = new HashMap<>();
		}

		long start = System.currentTimeMillis();
		Field sourceField;
		try {
			sourceField = types.getClass().getComponentType().getDeclaredField("source");
			sourceField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new Error(e); //Should never happen. TODO: write test
		}

		if (Platform.isBukkit()) {
			HashMap<Material, Double> similarities = new HashMap<>();
			for (T type : types) {
				try {
					String typeName = type.name();
					Material bestBukkitMaterialMatch = null;
					double bestSimilarity = 0;
					search: {
						for (Material material : Material.values()) {
							double similarity = similarity(typeName, material.name());

							if (similarity > bestSimilarity) {
								bestBukkitMaterialMatch = material;
								bestSimilarity = similarity;
								if (similarity == 1) {
									break search;
								}
							}
						}
					}
					if (bestBukkitMaterialMatch != null) {
						sourceField.set(type, bestBukkitMaterialMatch);

						//Set Bukkit -> Osmium map
						double currentSimilarity = similarities.getOrDefault(bestBukkitMaterialMatch, 0D);
						if (bestSimilarity > currentSimilarity) {
							bukkitItemTypes.put(bestBukkitMaterialMatch, type);
							similarities.put(bestBukkitMaterialMatch, bestSimilarity);
						}
					} else {
						OsmiumLogger.warn("Could not find item type for " + type.name());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			bukkitItemTypes.put(Material.AIR, ItemType.AIR);
		} else if (Platform.isSponge()) {
			for (T type : types) {
				try {
					for (Field field : blocks ? BlockTypes.class.getFields() : ItemTypes.class.getFields()) {
						if (!Modifier.isStatic(field.getModifiers()) || field.getType() != (blocks ? org.spongepowered.api.block.BlockType.class : org.spongepowered.api.item.ItemType.class)) {
							continue;
						}
						if (field.getName().equals(type.name())) {
							try {
								CatalogType spongeCatalogedItem = (CatalogType) field.get(null);
								sourceField.set(type, spongeCatalogedItem);
								spongeItemTypes.put(spongeCatalogedItem.getKey().getValue(), type);
								break;
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		OsmiumLogger.info("Mapped " + types.length + " item types in " + (System.currentTimeMillis() - start) + "ms");
	}

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 */
	public static double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) {
			longer = s2;
			shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 1.0;
		}
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	public static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}

}
