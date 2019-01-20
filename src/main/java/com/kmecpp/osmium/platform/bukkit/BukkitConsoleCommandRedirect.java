package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.craftbukkit.v1_13_R2.command.CraftConsoleCommandSender;

public class BukkitConsoleCommandRedirect extends CraftConsoleCommandSender {

	private org.bukkit.command.CommandSender output;

	public BukkitConsoleCommandRedirect(org.bukkit.command.CommandSender output) {
		this.output = output;
	}

	@Override
	public void sendMessage(String message) {
		output.sendMessage(message);
	}

	@Override
	public void sendRawMessage(String message) {
		output.sendMessage(message);
	}

}
