package com.kmecpp.osmium.api.inventory;

import java.util.List;

import org.bukkit.Material;
import org.spongepowered.api.item.ItemTypes;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.Abstraction;
import com.kmecpp.osmium.api.command.Chat;
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

	String getName();

	void setName(String name);

	//	void setDescriptionFormatted(String description);
	//
	//	void setDescriptionFormatted(List<String> description);

	void setDescription(List<String> description);

	void setLastDescriptionLine(String line);

	List<String> getDescription();

	int getAmount();

	void setAmount(int amount);

	default boolean isAir() {
		return getType() == ItemType.AIR;
	}

	//	default boolean isEmpty() {
	//		return getAmount() == 0;
	//	}

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
			if (type.source != null) {
				itemStack.setType(type);
			} else {
				new IllegalArgumentException(type.name() + " does not have a valid source on this platform! Defaulting to STONE").printStackTrace();
				itemStack.setType(ItemType.STONE);
			}
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
			itemStack.setName(Chat.style(name));
			return this;
		}

		public ItemStack build() {
			return itemStack;
		}

	}

}
