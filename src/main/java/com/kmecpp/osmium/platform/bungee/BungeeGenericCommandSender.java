package com.kmecpp.osmium.platform.bungee;

import com.kmecpp.osmium.api.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;

public class BungeeGenericCommandSender implements CommandSender {

	private net.md_5.bungee.api.CommandSender sender;

	public BungeeGenericCommandSender(net.md_5.bungee.api.CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public net.md_5.bungee.api.CommandSender getSource() {
		return sender;
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(new TextComponent(message));
	}

	@Override
	public boolean isOp() {
		return sender.hasPermission("*");
	}

	@Override
	public void setOp(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission("*");
	}

	@Override
	public String getName() {
		return sender.getName();
	}

}
