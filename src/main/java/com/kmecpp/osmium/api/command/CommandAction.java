package com.kmecpp.osmium.api.command;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class CommandAction implements Messageable {

	private CommandSender sender;
	private String command;
	private String[] args;

	public CommandAction(CommandSender sender, String command, String[] args) {
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

	public String getString(int index) {
		return args[index];
	}

	public Player getPlayer(int index) {
		return Osmium.getPlayer(args[index]).orElseThrow(() -> notFound("player", args[index]));
	}

	public OsmiumPlugin getPlugin(int index) {
		return Osmium.getPlugin(args[index]).orElseThrow(() -> notFound("plugin", args[index]));
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

	public void consumeArgument() {
		String[] temp = new String[args.length - 1];
		System.arraycopy(args, 1, temp, 0, temp.length);
		this.args = temp;
	}

	public boolean matches(int index, String... labels) {
		for (String label : labels) {
			if (args[index].equalsIgnoreCase(label)) {
				return true;
			}
		}
		return false;
	}

	//	public String getArg(int index) {
	//		return args[index];
	//	}

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

	public boolean isBaseCommand() {
		return args.length == 0;
	}

	public boolean hasArgs() {
		return args.length > 0;
	}

	public void handleError(Throwable t) {
		t.printStackTrace();
		sendMessage("&cError: " + t.getMessage());
	}

	public CommandException notFound(String name, String input) {
		return new CommandException("Could not find " + name + ": " + "'" + input + "'");
	}

	//	public void sendRawMessage(String style, String message) {
	//		sender.sendMessage(style, message);
	//	}

}
