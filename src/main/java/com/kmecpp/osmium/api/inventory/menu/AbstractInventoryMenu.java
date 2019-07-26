package com.kmecpp.osmium.api.inventory.menu;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.api.inventory.ItemType;

public abstract class AbstractInventoryMenu<T extends AbstractInventoryMenu<T>> {

	protected Inventory inventory;
	protected MenuItem[] items;

	public AbstractInventoryMenu() {
		this(9);
	}

	public AbstractInventoryMenu(int size) {
		this.items = new MenuItem[size];
	}

	public MenuItem[] getItems() {
		return items;
	}

	public abstract void send(Player player);

	public T setRow(int row, ItemType type) {
		return setRow(row, MenuItem.of(type));
	}

	public T set(int index, ItemType type) {
		return set(index, MenuItem.of(type));
	}

	public void remove(int index) {
		if (inventory != null) {
			inventory.setItem(index - 1, null);
		}
		items[index - 1] = null;
	}

	@SuppressWarnings("unchecked")
	public T setRow(int row, MenuItem item) {
		for (int i = 9 * (row - 1) + 1; i < 9 * row + 1; i++) {
			set(i, item);
		}
		return (T) this;
	}

	public void setCenter(int row, int amount, MenuItem item) {
		amount = Math.min(amount, 5);
		for (int i = 9 * (row - 1) + 1 + (5 - amount); i < 9 * (row - 1) + 1 + (4 + amount); i++) {
			set(i, item);
		}
	}

	public T set(int row, int col, ItemType type) {
		return set(row, col, MenuItem.of(type));
	}

	@SuppressWarnings("unchecked")
	public T set(int row, int col, MenuItem item) {
		set(9 * (row - 1) + col, item);
		//		items[9 * (row - 1) + (col - 1)] = item;
		//		if (inventory != null) {
		//			inventory.setItem(9 * (row - 1) + (col - 1), item.getItemStack());
		//		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T set(int index, MenuItem item) {
		items[index - 1] = item;
		if (inventory != null) {
			inventory.setItem(index - 1, item.getItemStack());
		}
		return (T) this;
	}

	public MenuItem get(int index) {
		return items[index - 1];
	}

	public MenuItem get(int row, int col) {
		return items[9 * (row - 1) + (col - 1)];
	}

}
