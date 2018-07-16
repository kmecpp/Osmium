package com.kmecpp.osmium.api.command;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.entity.Player;

public class CommandEvent implements Messageable {

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
		return Osmium.getPlayer(args[index]).orElseThrow(() -> CommandException.PLAYER_NOT_FOUND);
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

	public String getArg(int index) {
		return args[index];
	}

	public String[] getArgs() {
		return args;
	}

	@Override
	public void sendRawMessage(String message) {
		sender.sendMessage(message);
	}

	public int args() {
		return args.length;
	}

	public boolean isEmpty() {
		return args.length == 0;
	}

	public boolean hasArgs() {
		return args.length > 0;
	}

	//	public void sendRawMessage(String style, String message) {
	//		sender.sendMessage(style, message);
	//	}

}
