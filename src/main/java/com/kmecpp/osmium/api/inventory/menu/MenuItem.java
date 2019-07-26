package com.kmecpp.osmium.api.inventory.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;

public class MenuItem {

	private ItemStack itemStack;
	private ClickHandler clickHandler;

	private ItemCost cost;

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
		return itemStack.getName();
	}

	public MenuItem setName(String name) {
		itemStack.setName(Chat.style(name));
		return this;
	}

	public MenuItem setDescription(String description) {
		itemStack.setDescription(Chat.styleLines(description));
		return this;
	}

	public MenuItem setLastDescriptionLine(String line) {
		itemStack.setLastDescriptionLine(line);
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

	public MenuItem setCost(Resource resource, int amount) {
		this.cost = new ItemCost(resource, amount);

		//		String name;
		//		Chat color;
		//		switch (type) {
		//		case IRON_INGOT:
		//			color = Chat.WHITE;
		//			name = "Iron";
		//			break;
		//		case GOLD_INGOT:
		//			color = Chat.GOLD;
		//			name = "Gold";
		//		case DIAMOND:
		//			color = Chat.AQUA;
		//			name = "Diamond";
		//		case EMERALD:
		//			color = Chat.GREEN;
		//			name = "Emerald";
		//		default:
		//			name = type.name();
		//			color = Chat.GRAY;
		//		}
		return this;
	}

	//	public MenuItem addCost(String name, ItemType type, int amount) {
	//		if (costs == null) {
	//			costs = new EnumMap<>(ItemType.class);
	//		}
	//		costs.put(type, new ItemCost(name, amount));
	//		return this;
	//	}

	public ItemCost getCost() {
		return cost;
	}

	public boolean buy(Player player) {
		if (cost == null) {
			return false;
		}
		if (player.getInventory().containsAtLeast(cost.getType().getExample(), cost.getAmount())) {
			player.getInventory().addItem(this.getItemStack());
			player.getInventory().take(cost.getType().getExample().getType(), cost.getAmount());
			return true;
		}
		return false;
		//		int amount = 0;
		//		HashMap<ItemStack, Integer> usedItems = new HashMap<>();
		//		int availableSlot = -1;
		//		int slotIndex = -1;
		//		for (ItemStack item : player.getInventory().getItems()) {
		//			slotIndex++;
		//			if (item == null || item.isAir()) {
		//				availableSlot = slotIndex;
		//				continue;
		//			}
		//			Resource resource = Resource.fromType(item.getType());
		//			if (resource != null && resource == cost.resource) {
		//				amount += item.getAmount();
		//				usedItems.put(item, slotIndex);
		//
		//				//If this stack will be used up while purchasing the item
		//				if (amount < cost.getAmount() && availableSlot == -1) {
		//					availableSlot = slotIndex;
		//				} else if (amount >= cost.getAmount()) {
		//					System.out.println("HAS ENOUGH!");
		//					break;
		//				}
		//			}
		//		}
		//		if (amount >= cost.getAmount() && availableSlot != -1) {
		//			int amountRemaining = cost.getAmount();
		//			System.out.println("DO BUY!");
		//			for (Entry<ItemStack, Integer> usedEntry : usedItems.entrySet()) {
		//				ItemStack used = usedEntry.getKey();
		//				int usedIndex = usedEntry.getValue();
		//
		//				int take = Math.min(used.getAmount(), amountRemaining);
		//				used.setAmount(used.getAmount() - take);
		//				player.getInventory().setItem(usedIndex, used);
		//				System.out.println("NEW AMOUNT: " + used.getAmount());
		//				amountRemaining -= take;
		//				if (amountRemaining <= 0) {
		//					break;
		//				}
		//			}
		//			player.getInventory().setItem(availableSlot, this.getItemStack());
		//			System.out.println("SET ITEM!");
		//
		//						((org.bukkit.entity.Player)player.getSource()).getInventory()..addItem(items)
		//			if (Platform.isBukkit()) {
		//				player.playSound(SoundType.ENTITY_ARROW_HIT, 1, 1);
		//			}
		//			return true;
		//		}
		//		return false;
	}

	public MenuItem setHandler(ClickHandler handler) {
		this.clickHandler = handler;
		return this;
	}

	public static Builder builder() {
		return new Builder();
	}

	public class ItemCost {

		private Resource resource;
		private int amount;

		private String enabledName;
		private String disabledName;
		private ArrayList<String> enabledMeta;
		private ArrayList<String> disabledMeta;

		public ItemCost(Resource resource, int amount) {
			this.resource = resource;
			this.amount = amount;

			this.enabledName = Chat.GREEN + MenuItem.this.getName();
			this.disabledName = Chat.RED + MenuItem.this.getName();

			this.enabledMeta = new ArrayList<>();
			this.disabledMeta = new ArrayList<>();

			List<String> cost = Arrays.asList(Chat.GRAY + "Cost: " + resource.getColor() + amount + " " + resource.getName(), "", "");
			this.enabledMeta.addAll(cost);
			this.disabledMeta.addAll(cost);

			this.enabledMeta.add(Chat.GREEN + "Click to purchase this item!");
			this.disabledMeta.add(Chat.RED + "You do not have enough " + resource.getName() + "!");
		}

		public void update(ItemStack itemStack, boolean enabled) {
			itemStack.setName(enabled ? enabledName : disabledName);
			itemStack.setDescription(enabled ? enabledMeta : disabledMeta);
		}

		public Resource getType() {
			return resource;
		}

		public int getAmount() {
			return amount;
		}

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
			item.itemStack.setName(Chat.style(name));
			return this;
		}

		public Builder description(String description) {
			item.itemStack.setDescription(Chat.styleLines(description));
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
