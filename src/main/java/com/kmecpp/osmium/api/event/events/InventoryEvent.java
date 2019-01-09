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

	public interface Interact extends InventoryEvent {

		boolean isClick();

		boolean isDrag();

	}

	public interface Click extends Interact {

		@Override
		default boolean isClick() {
			return true;
		}

		@Override
		default boolean isDrag() {
			return false;
		}

		int getSlot();

		boolean isLeftClick();

		boolean isRightClick();

		ClickType getClick();

	}

	public interface Drag extends Interact {

		@Override
		default boolean isClick() {
			return false;
		}

		@Override
		default boolean isDrag() {
			return true;
		}

		boolean isEvenDrag();

		boolean isSingleDrag();

		Set<Integer> getSlots();

	}

}
