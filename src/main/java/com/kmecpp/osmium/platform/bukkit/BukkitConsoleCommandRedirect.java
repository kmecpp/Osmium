package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.craftbukkit.v1_13_R2.command.CraftConsoleCommandSender;

import com.kmecpp.osmium.api.command.CommandSender;

public class BukkitConsoleCommandRedirect extends CraftConsoleCommandSender {

	private CommandSender output;

	public BukkitConsoleCommandRedirect(CommandSender output) {
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
