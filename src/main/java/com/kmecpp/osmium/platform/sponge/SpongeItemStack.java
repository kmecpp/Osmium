package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;
import com.kmecpp.osmium.api.util.Reflection;

public class SpongeItemStack implements ItemStack {

	private org.spongepowered.api.item.inventory.ItemStack itemStack;
	private ItemType type;

	public SpongeItemStack(org.spongepowered.api.item.inventory.ItemStack itemStack) {
		this.itemStack = itemStack != null ? itemStack : org.spongepowered.api.item.inventory.ItemStack.builder().itemType(ItemTypes.AIR).build();
		this.type = SpongeAccess.getItemType(itemStack);
	}

	@Override
	public org.spongepowered.api.item.inventory.ItemStack getSource() {
		return itemStack;
	}

	@Override
	public ItemType getType() {
		return type;
	}

	@Override
	public void setType(ItemType type) {
		itemStack = org.spongepowered.api.item.inventory.ItemStack.builder().from(itemStack).itemType(Reflection.cast(type.getSource())).build();
		//		itemStack.ty(Reflection.cast(type.getSource()));
	}

	@Override
	public int getDamage() {
		return itemStack.get(Keys.ITEM_DURABILITY).orElse(0);
	}

	@Override
	public void setDamage(int damage) {
		itemStack.offer(Keys.ITEM_DURABILITY, damage);
	}

	@Override
	public String getDisplayName() {
		return itemStack.get(Keys.DISPLAY_NAME).get().toString();
		//		return itemStack.get(DisplayNameData.class).get().displayName().get().toString();
	}

	@Override
	public void setDisplayName(String name) {
		itemStack.offer(Keys.DISPLAY_NAME, Text.of(name));
	}

	@Override
	public int getAmount() {
		return itemStack.getQuantity();
	}

	@Override
	public void setAmount(int amount) {
		itemStack.setQuantity(amount);
	}

}
