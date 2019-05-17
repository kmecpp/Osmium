package com.kmecpp.osmium.api.inventory;

import java.util.HashMap;

import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;

import com.kmecpp.osmium.api.platform.Platform;

public enum ClickType {

	LEFT,
	RIGHT,
	MIDDLE,
	SHIFT_LEFT,
	SHIFT_RIGHT,
	NUMBER_KEY,
	CREATIVE,

	;

	private Object source;

	private static final HashMap<Object, ClickType> map = new HashMap<>();

	static {
		if (Platform.isBukkit()) {
			LEFT.source = org.bukkit.event.inventory.ClickType.LEFT;
			RIGHT.source = org.bukkit.event.inventory.ClickType.RIGHT;
			MIDDLE.source = org.bukkit.event.inventory.ClickType.MIDDLE;
			SHIFT_LEFT.source = org.bukkit.event.inventory.ClickType.SHIFT_LEFT;
			SHIFT_RIGHT.source = org.bukkit.event.inventory.ClickType.SHIFT_RIGHT;
			NUMBER_KEY.source = org.bukkit.event.inventory.ClickType.NUMBER_KEY;
			CREATIVE.source = org.bukkit.event.inventory.ClickType.CREATIVE;
			CREATIVE.source = org.bukkit.event.inventory.ClickType.CREATIVE;
		} else if (Platform.isSponge()) {
			LEFT.source = ClickInventoryEvent.Primary.class;
			RIGHT.source = ClickInventoryEvent.Secondary.class;
			MIDDLE.source = ClickInventoryEvent.Middle.class;
			SHIFT_LEFT.source = ClickInventoryEvent.Shift.Primary.class;
			SHIFT_RIGHT.source = ClickInventoryEvent.Shift.Secondary.class;
			NUMBER_KEY.source = ClickInventoryEvent.NumberPress.class;
			CREATIVE.source = ClickInventoryEvent.Creative.class;
		}

		for (ClickType type : values()) {
			map.put(type.source, type);
		}
	}

	public boolean isLeft() {
		return this == LEFT || this == SHIFT_LEFT;
	}

	public boolean isRight() {
		return this == RIGHT || this == SHIFT_RIGHT;
	}

	public static ClickType fromSource(Object source) {
		return map.get(source);
	}

}
