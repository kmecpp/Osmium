package com.kmecpp.osmium.api.event.events;

import java.util.Set;

import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;
import com.kmecpp.osmium.api.inventory.ClickType;
import com.kmecpp.osmium.api.inventory.Inventory;

public interface InventoryEvent extends PlayerEvent, Cancellable {

	Inventory getInventory();

	public interface Open extends InventoryEvent {
	}

	public interface Close extends InventoryEvent {
	}

	public interface Click extends InventoryEvent {

		int getSlot();

		boolean isLeftClick();

		boolean isRightClick();

		ClickType getClick();

	}

	public interface Drag extends InventoryEvent {

		boolean isEvenDrag();

		boolean isSingleDrag();

		Set<Integer> getSlots();

	}

}
