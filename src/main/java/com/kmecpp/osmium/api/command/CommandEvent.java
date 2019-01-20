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
		String input = get(index);
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new CommandException(StringUtil.isMathematicalInteger(input)
					? "Expected an integer but given value was too large: '" + input + "'"
					: "Expected an integer recieved: '" + input + "'");
		}
	}

	private void checkIndex(int index) {
		if (index < 0) {
			throw new CommandException("Internal command error. Tried to retrieve index: " + index);
		} else if (index >= args.length) {
			//			throw new CommandException("Expected at least " + index + 1 + " arguments");
			throw CommandException.USAGE_ERROR;
		}
	}

	private String get(int index) {
		checkIndex(index);
		return args[index];
	}

	public double getDouble(int index) {
		String input = get(index);
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new CommandException("Expected a decimal but got: '" + input + "'");
		}
	}

	public boolean getBoolean(int index) {
		String input = get(index);
		if (StringUtil.startsWithIgnoreCase(input, "true", "1", "yes")) {
			return true;
		} else if (StringUtil.startsWithIgnoreCase(input, "false", "0", "no")) {
			return false;
		} else {
			throw new CommandException("Expected a boolean but got: '" + input + "'");
		}
	}

	public String getString(int index) {
		return get(index);
	}

	public User getUser(int index) {
		return Osmium.getUser(get(index)).orElseThrow(() -> notFound("user", get(index)));
	}

	public Player getPlayer(int index) {
		return Osmium.getPlayer(get(index)).orElseThrow(() -> notFound("player", get(index)));
	}

	public World getWorld(int index) {
		return Osmium.getWorld(get(index)).orElseThrow(() -> notFound("world", get(index)));
	}

	public OsmiumPlugin getPlugin(int index) {
		return Osmium.getPlugin(get(index)).orElseThrow(() -> notFound("plugin", get(index)));
	}

	public Location getLocation(int index) {
		return sender instanceof Player
				? new Location(((Player) sender).getWorld(), getDouble(index), getDouble(index + 1), getDouble(index + 2))
				: new Location(getWorld(index), getDouble(index + 1), getDouble(index + 2), getDouble(index + 3));
	}

	public String getRemainingJoined(int index) {
		checkIndex(index);
		return String.join(" ", Arrays.copyOfRange(args, index, args.length));
	}

	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
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

	public boolean equalsIgnoreCase(int index, String... labels) {
		for (String label : labels) {
			if (get(index).equalsIgnoreCase(label)) {
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
		send("&cError: " + t.getMessage());
	}

	public CommandException notFound(String expectedLabel, String input) {
		return new CommandException("Could find " + expectedLabel + ": '" + input + "'");
	}

	//	public void sendRawMessage(String style, String message) {
	//		sender.sendMessage(style, message);
	//	}

}
