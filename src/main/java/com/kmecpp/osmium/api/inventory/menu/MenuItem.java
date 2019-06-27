package com.kmecpp.osmium.api.inventory.menu;

import java.util.List;

import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;

public class MenuItem {

	private ItemStack itemStack;
	private ClickHandler clickHandler;

	private MenuItem(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public static MenuItem of(ItemType type) {
		return of(null, type);
	}

	public static MenuItem of(ItemType type, int damage) {
		return of(null, type, damage);
	}

	public static MenuItem of(String name, ItemType type) {
		return of(name, type, 0);
	}

	public static MenuItem of(String name, ItemType type, int damage) {
		ItemStack.Builder builder = ItemStack.builder();
		builder.type(type);
		builder.damage(damage);
		if (name != null) {
			builder.name(name);
		}
		return new MenuItem(builder.build());
	}

	public static MenuItem of(ItemStack itemStack) {
		return new MenuItem(itemStack);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public ClickHandler getClickHandler() {
		return clickHandler;
	}

	public String getName() {
		return itemStack.getDisplayName();
	}

	public MenuItem setName(String name) {
		itemStack.setDisplayName(Chat.style(name));
		return this;
	}

	public MenuItem setDescription(String description) {
		itemStack.setDescription(description);
		return this;
	}

	public List<String> getDescription() {
		return itemStack.getDescription();
	}

	public MenuItem setAmount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public int getAmount() {
		return itemStack.getAmount();
	}

	public MenuItem setHandler(ClickHandler handler) {
		this.clickHandler = handler;
		return this;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private MenuItem item = new MenuItem(ItemStack.of(ItemType.AIR));

		public Builder type(ItemType type) {
			item.itemStack.setType(type);
			return this;
		}

		public Builder item(ItemStack itemStack) {
			item.itemStack = itemStack;
			return this;
		}

		public Builder name(String name) {
			item.itemStack.setDisplayName(Chat.style(name));
			return this;
		}

		public Builder description(String description) {
			item.itemStack.setDescription(description);
			return this;
		}

		public Builder handler(ClickHandler e) {
			item.clickHandler = e;
			return this;
		}

		public MenuItem build() {
			return item;
		}
	}

}
