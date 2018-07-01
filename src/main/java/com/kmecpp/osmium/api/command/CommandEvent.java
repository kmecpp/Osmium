package com.kmecpp.osmium.api.command;

import com.kmecpp.osmium.api.CommandSender;
import com.kmecpp.osmium.api.Player;

public class CommandEvent {

	private CommandSender sender;
	private String command;
	private String[] args;

	public CommandEvent(CommandSender sender, String command, String[] args) {
		this.sender = sender;
		this.command = command;
		this.args = args;
	}

	public Player getPlayer() {
		return (Player) sender;
	}

	public CommandSender getSender() {
		return sender;
	}

	public String getCommand() {
		return command;
	}

	public String[] getArgs() {
		return args;
	}

}
