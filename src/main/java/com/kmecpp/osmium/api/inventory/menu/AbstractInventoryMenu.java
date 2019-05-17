package com.kmecpp.osmium.api.inventory.menu;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.ItemType;

public abstract class AbstractInventoryMenu<T extends AbstractInventoryMenu<T>> {

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

	@SuppressWarnings("unchecked")
	public T setRow(int row, ItemType type) {
		for (int i = row * 9; i < (row + 1) * 9; i++) {
			items[i] = MenuItem.of(type);
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T set(int index, MenuItem item) {
		items[index] = item;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T set(int row, int col, MenuItem item) {
		items[9 * row + col] = item;
		return (T) this;
	}

}
