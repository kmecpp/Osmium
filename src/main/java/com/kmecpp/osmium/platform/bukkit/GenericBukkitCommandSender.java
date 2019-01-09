package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.command.CommandSender;

/**
 * Temporary class for command sources: Proxy, Rcon, Remote, Sign
 * 
 * Just because it would take too long to implement all of them
 * 
 * TODO: It's possible Sign should be a BlockCommandSender
 */
public class GenericBukkitCommandSender implements CommandSender {

	private org.bukkit.command.CommandSender sender;

	public GenericBukkitCommandSender(org.bukkit.command.CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public org.bukkit.command.CommandSender getSource() {
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
