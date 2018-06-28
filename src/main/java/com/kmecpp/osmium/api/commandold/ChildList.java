package com.kmecpp.osmium.api.commandold;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

public class ChildList {

	private Text title;
	private TextColor dividerColor;
	private TextColor commandColor;
	private TextColor descriptionColor;

	public ChildList(Text title, TextColor dividerColor, TextColor commandColor, TextColor descriptionColor) {
		this.title = title;
		this.dividerColor = dividerColor;
		this.commandColor = commandColor;
		this.descriptionColor = descriptionColor;
	}

	public Text getTitle() {
		return title;
	}

	public TextColor getDividerColor() {
		return dividerColor;
	}

	public TextColor getCommandColor() {
		return commandColor;
	}

	public TextColor getDescriptionColor() {
		return descriptionColor;
	}

}