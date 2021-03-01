package com.kmecpp.osmium;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import com.kmecpp.osmium.api.Abstraction;

public class OsmiumRegistry {

	private static HashMap<Class<?>, HashMap<String, Object>> map = new HashMap<>();//

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T> & Abstraction> T fromSource(Class<T> cls, Object source) {
		return (T) map.get(cls).get(String.valueOf(source));
	}

	public static <E extends Enum<E> & Abstraction> void initSources(Class<E> enumClass) {
		Field sourceField;
		try {
			sourceField = enumClass.getDeclaredField("source");
			sourceField.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new Error(e);
		}

		map.computeIfAbsent(enumClass, (key) -> new HashMap<>());
		HashMap<String, Object> typeMap = map.get(enumClass);

		for (Enum<E> type : enumClass.getEnumConstants()) {
			if (Platform.isBukkit()) {
				for (org.bukkit.entity.EntityType bukkitType : org.bukkit.entity.EntityType.values()) {
					if (type.name().equals(bukkitType.name())) {
						try {
							sourceField.set(type, bukkitType);
							typeMap.put(bukkitType.name(), type);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			} else if (Platform.isSponge()) {
				for (Field field : EntityTypes.class.getDeclaredFields()) {
					if (!Modifier.isStatic(field.getModifiers()) || field.getType() != InventoryArchetype.class) {
						continue;
					}
					if (field.getName().equals(type.name())) {
						try {
							CatalogType spongeCatalogedItem = (CatalogType) field.get(null);
							sourceField.set(type, spongeCatalogedItem);
							typeMap.put(spongeCatalogedItem.getId(), type);
							break;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
