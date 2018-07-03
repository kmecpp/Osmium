package com.kmecpp.osmium.api.command;

import com.kmecpp.osmium.api.entity.Player;

public class CommandEvent {

	private CommandSender sender;
	private String command;
	private String[] args;

	public CommandEvent(CommandSender sender, String command, String[] args) {
		this.sender = sender;
		this.command = command;
		this.args = args;
	}

	public int getInt(int index) {
		return Integer.parseInt(args[index]);
	}

	public long getLong(int index) {
		return Long.parseLong(args[index]);
	}

	public float getFloat(int index) {
		return Float.parseFloat(args[index]);
	}

	public double getDouble(int index) {
		return Double.parseDouble(args[index]);
	}

	public boolean getBoolean(int index) {
		return Boolean.parseBoolean(args[index]);
	}

	public Player getPlayer(int index) {
		//TODO
		return null;
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
