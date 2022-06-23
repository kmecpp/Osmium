package com.kmecpp.osmium.platform.sponge;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.api.inventory.ItemType;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.platform.SpongeAccess;

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
	public String getName() {
		return itemStack.get(Keys.DISPLAY_NAME).get().toString();
		//		return itemStack.get(DisplayNameData.class).get().displayName().get().toString();
	}

	@Override
	public void setName(String name) {
		itemStack.offer(Keys.DISPLAY_NAME, Text.of(name));
	}

	@Override
	public List<String> getDescription() {
		List<Text> textList = itemStack.get(Keys.ITEM_LORE).orElse(new ArrayList<>());
		ArrayList<String> result = new ArrayList<>();
		for (Text text : textList) {
			result.add(text.toString());
		}
		return result;
	}

	//	@Override
	//	public void setDescriptionFormatted(String description) {
	//		ArrayList<String> lines = Chat.styleLines(description);
	//		ArrayList<Text> text = new ArrayList<>(lines.size());
	//		for (String line : lines) {
	//			text.add(SpongeAccess.getText(line));
	//		}
	//		itemStack.offer(Keys.ITEM_LORE, text);
	//	}
	//
	//	@Override
	//	public void setDescriptionFormatted(List<String> description) {
	//		ArrayList<Text> text = new ArrayList<>(description.size());
	//		for (String line : description) {
	//			text.add(SpongeAccess.getText(Chat.style(line)));
	//		}
	//		itemStack.offer(Keys.ITEM_LORE, text);;
	//	}

	@Override
	public void setDescription(List<String> description) {
		ArrayList<Text> text = new ArrayList<>(description.size());
		for (String line : description) {
			text.add(SpongeAccess.getText(line));
		}
		itemStack.offer(Keys.ITEM_LORE, text);;
	}

	@Override
	public void setLastDescriptionLine(String line) {
		List<Text> textList = itemStack.get(Keys.ITEM_LORE).orElse(new ArrayList<>());
		if (textList.isEmpty()) {
			textList.add(Text.of(Chat.style(line)));
		} else {
			textList.set(textList.size() - 1, Text.of(Chat.style(line)));
		}
		itemStack.offer(Keys.ITEM_LORE, textList);
	}

	@Override
	public int getAmount() {
		return itemStack.getQuantity();
	}

	@Override
	public void setAmount(int amount) {
		itemStack.setQuantity(amount);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SpongeItemStack && itemStack.equals(((SpongeItemStack) obj).itemStack);
	}

}
