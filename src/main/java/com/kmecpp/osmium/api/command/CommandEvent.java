package com.kmecpp.osmium.api.command;

import java.util.Arrays;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.StringUtil;

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
		String input = args[index];
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new CommandException(StringUtil.isMathematicalInteger(input)
					? "Expected an integer but given value was too large: '" + input + "'"
					: "Expected an integer recieved: '" + input + "'");
		}
	}

	public double getDouble(int index) {
		String input = args[index];
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new CommandException("Expected a decimal but got: '" + input + "'");
		}
	}

	public boolean getBoolean(int index) {
		String input = args[index];
		if (StringUtil.startsWithIgnoreCase(input, "true", "1", "yes")) {
			return true;
		} else if (StringUtil.startsWithIgnoreCase(input, "false", "0", "no")) {
			return false;
		} else {
			throw new CommandException("Expected a boolean but got: '" + input + "'");
		}
	}

	public String getString(int index) {
		return args[index];
	}

	public Player getPlayer(int index) {
		return Osmium.getPlayer(args[index]).orElseThrow(() -> notFound("player", args[index]));
	}

	public World getWorld(int index) {
		return Osmium.getWorld(args[index]).orElseThrow(() -> notFound("world", args[index]));
	}

	public OsmiumPlugin getPlugin(int index) {
		return Osmium.getPlugin(args[index]).orElseThrow(() -> notFound("plugin", args[index]));
	}

	public Location getLocation(int index) {
		return sender instanceof Player
				? new Location(((Player) sender).getWorld(), getDouble(index), getDouble(index + 1), getDouble(index + 2))
				: new Location(getWorld(index), getDouble(index + 1), getDouble(index + 2), getDouble(index + 3));
	}

	public String getRemainingJoined(int index) {
		return String.join(" ", Arrays.copyOfRange(args, index, args.length));
	}

	public User getUser(int index) {
		return Osmium.getUser(args[index]).orElseThrow(() -> notFound("user", args[index]));
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

	public String[] getArgs() {
		return args;
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(message);
	}

	public int size() {
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
		sendStyledMessage("&cError: " + t.getMessage());
	}

	public CommandException notFound(String expectedLabel, String input) {
		return new CommandException("Could find " + expectedLabel + ": '" + input + "'");
	}

	//	public void sendRawMessage(String style, String message) {
	//		sender.sendMessage(style, message);
	//	}

}
