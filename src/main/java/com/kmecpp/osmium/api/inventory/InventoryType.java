package com.kmecpp.osmium.api.inventory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.Abstraction;

public enum InventoryType implements Abstraction {

	/**
	 * A Chest. Sizes from 9x1 to 9x6 are allowed. The default is 9x3.
	 *
	 * <p>
	 * When displaying the inventory the actual arrangement of slot
	 * does not matter. This means, that when creating a 3x3 CHEST inventory it
	 * will still be displayed as 9x1 to the player.
	 * </p>
	 */
	CHEST,

	/**
	 * A DoubleChest. Sizes 9x1 to 9x6 are allowed. The default is 9x6.
	 */
	DOUBLE_CHEST,

	/**
	 * A Hopper. The size is always 5x1
	 */
	HOPPER,

	/**
	 * A Dispenser or Dropper. The size is always 3x3
	 */
	DISPENSER,

	/**
	 * A Workbench. The size is always 3x3 + 1 OutputSlot
	 */
	WORKBENCH,

	// Slot-based

	/**
	 * A Furnace. The size is always 3 slots
	 */
	FURNACE,

	/**
	 * A EnchantingTable. The size is always 2 slots.
	 */
	ENCHANTING_TABLE,

	/**
	 * A Anvil. The size is always 3 slots.
	 */
	ANVIL,

	/**
	 * A BrewingStand. 5 Slots.
	 */
	BREWING_STAND,

	/**
	 * A Beacon. The size is always one slot.
	 */
	BEACON,

	// Entity

	/**
	 * A RideableHorse, Donkey or Mule usually 2 Slots.
	 */
	HORSE,

	/**
	 * A Villager. The size is always 3 slots.
	 */
	VILLAGER,

	/**
	 * A Donkey or Mule with Chest. 2 Slots and 5x3 Chest
	 * Needs a horse as carrier to show to player in Vanilla.
	 */
	HORSE_WITH_CHEST,

	// Player

	/**
	 * A Player. Includes 9x3 main inventory, 9x1 Hotbar, 4 Armorslots and 2x2
	 * Crafting area.
	 *
	 * <p>
	 * Cannot be opened by the server in Vanilla.
	 * </p>
	 */
	PLAYER,

	UNKNOWN;

	private Object source;
	private static HashMap<?, InventoryType> sourceMap = new HashMap<>();

	@Override
	public Object getSource() {
		return source;
	}

	static {
		for (InventoryType type : values()) {
			if (Platform.isBukkit()) {
				for (org.bukkit.event.inventory.InventoryType bukkitType : org.bukkit.event.inventory.InventoryType.values()) {
					if (type.name().equals(bukkitType.name())) {
						type.source = bukkitType;
					}
				}
			} else if (Platform.isSponge()) {
				Field sourceField;
				try {
					sourceField = InventoryType.class.getDeclaredField("source");
					sourceField.setAccessible(true);
				} catch (NoSuchFieldException | SecurityException e) {
					throw new Error(e); //Should never happen. TODO: write test?
				}

				for (Field field : InventoryArchetypes.class.getDeclaredFields()) {
					if (!Modifier.isStatic(field.getModifiers()) || field.getType() != InventoryArchetype.class) {
						continue;
					}
					if (field.getName().equals(type.name())) {
						try {
							CatalogType spongeCatalogedItem = (CatalogType) field.get(null);
							sourceField.set(type, spongeCatalogedItem);
							break;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static InventoryType fromSource(Object source) {
		return sourceMap.get(source);
	}

}
