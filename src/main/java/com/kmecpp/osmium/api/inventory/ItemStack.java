package com.kmecpp.osmium.api.inventory;

import java.util.List;

import org.bukkit.Material;
import org.spongepowered.api.item.ItemTypes;

import com.kmecpp.osmium.api.Abstraction;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.bukkit.BukkitItemStack;
import com.kmecpp.osmium.platform.sponge.SpongeItemStack;

public interface ItemStack extends Abstraction {

	//	private ItemType type;
	//	private int amount;
	//
	//	public ItemType getType() {
	//		return type;
	//	}
	//
	//	public int getAmount() {
	//		return amount;
	//	}
	//
	//	public boolean isEmpty() {
	//		return amount == 0;
	//	}

	//	String getId();
	//
	ItemType getType();

	void setType(ItemType type);

	int getDamage();

	void setDamage(int damage);

	String getDisplayName();

	void setDisplayName(String name);

	void setDescription(String description);

	List<String> getDescription();

	int getAmount();

	void setAmount(int amount);

	default boolean isAir() {
		return getType() == ItemType.AIR;
	}

	default boolean isEmpty() {
		return getAmount() == 0;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static ItemStack of(ItemType type) {
		return of(type, 1);
	}

	public static ItemStack of(ItemType type, int amount) {
		return Platform.isBukkit()
				? new BukkitItemStack(new org.bukkit.inventory.ItemStack(Reflection.cast(type.source), amount))
				: new SpongeItemStack(org.spongepowered.api.item.inventory.ItemStack.of(Reflection.cast(type.source), 1));
	}

	public static class Builder {

		private ItemStack itemStack;

		public Builder() {
			itemStack = Platform.isBukkit()
					? new BukkitItemStack(new org.bukkit.inventory.ItemStack(Material.AIR))
					: new SpongeItemStack(org.spongepowered.api.item.inventory.ItemStack.of(ItemTypes.AIR, 1));
		}

		public Builder type(ItemType type) {
			itemStack.setType(type);
			return this;
		}

		public Builder amount(int amount) {
			itemStack.setAmount(amount);
			return this;
		}

		public Builder damage(int damage) {
			itemStack.setDamage(damage);
			return this;
		}

		public Builder name(String name) {
			itemStack.setDisplayName(Chat.style(name));
			return this;
		}

		public ItemStack build() {
			return itemStack;
		}

	}

}
