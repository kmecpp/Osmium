package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.command.ConsoleCommandSender;

public class BukkitConsoleCommandSender implements ConsoleCommandSender {

	private org.bukkit.command.ConsoleCommandSender sender;

	public BukkitConsoleCommandSender(org.bukkit.command.ConsoleCommandSender sender) {
		this.sender = sender;
	}

	@Override
	public org.bukkit.command.ConsoleCommandSender getSource() {
		return sender;
	}

	@Override
	public boolean isOp() {
		return sender.isOp();
	}

	@Override
	public void setOp(boolean value) {
		sender.setOp(value);
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(message);
	}

}
