package com.kmecpp.osmium.api.command;

import java.util.Arrays;
import java.util.Optional;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.StringUtil;

public class CommandEvent implements Messageable {

	private Command command;
	private SimpleCommand subCommand;
	private CommandSender sender;
	private String baseLabel;
	private String argLabel;
	private String[] args;

	public CommandEvent(Command command, CommandSender sender, String baseLabel, String[] args) {
		this.command = command;
		this.sender = sender;
		this.baseLabel = baseLabel;
		this.args = args;
	}

	public Command getCommand() {
		return command;
	}

	public Player getPlayer() {
		return (Player) sender;
	}

	public CommandSender getSender() {
		return sender;
	}

	public String getBaseLabel() {
		return baseLabel;
	}

	public String getArgLabel() {
		return argLabel;
	}

	public Optional<SimpleCommand> getSubCommand() {
		return Optional.ofNullable(subCommand);
	}

	void setSubCommand(SimpleCommand subCommand) {
		this.subCommand = subCommand;
	}

	public String[] getArgs() {
		return args;
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

	public int getInt(int index, int def) {
		return index < args.length ? getInt(index) : def;
	}

	public double getDouble(int index) {
		String input = get(index);
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new CommandException("Expected a decimal but got: '" + input + "'");
		}
	}

	public double getDouble(int index, double def) {
		return index < args.length ? getDouble(index) : def;
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

	public boolean getBoolean(int index, boolean def) {
		return index < args.length ? getBoolean(index) : def;
	}

	public String getString(int index) {
		return get(index);
	}

	public String getString(int index, String def) {
		return index < args.length ? getString(index) : def;
	}

	public User getUser(int index) {
		return Osmium.getUser(get(index)).orElseThrow(() -> notFound("user", get(index)));
	}

	public User getUser(int index, User def) {
		return index < args.length ? getUser(index) : def;
	}

	public Player getPlayer(int index) {
		return Osmium.getPlayer(get(index)).orElseThrow(() -> notFound("player", get(index)));
	}

	public Player getPlayer(int index, Player def) {
		return index < args.length ? getPlayer(index) : def;
	}

	public World getWorld(int index) {
		return Osmium.getWorld(get(index)).orElseThrow(() -> notFound("world", get(index)));
	}

	public World getWorld(int index, World def) {
		return index < args.length ? getWorld(index) : def;
	}

	public OsmiumPlugin getPlugin(int index) {
		return Osmium.getPlugin(get(index)).orElseThrow(() -> notFound("plugin", get(index)));
	}

	public OsmiumPlugin getPlugin(int index, OsmiumPlugin def) {
		return index < args.length ? getPlugin(index) : def;
	}

	public Location getLocation(int index) {
		return sender instanceof Player
				? new Location(((Player) sender).getWorld(), getDouble(index), getDouble(index + 1), getDouble(index + 2))
				: new Location(getWorld(index), getDouble(index + 1), getDouble(index + 2), getDouble(index + 3));
	}

	public Location getLocation(int index, Location def) {
		return index < args.length ? getLocation(index) : def;
	}

	public String getRemainingJoined(int index) {
		checkIndex(index);
		return String.join(" ", Arrays.copyOfRange(args, index, args.length));
	}

	public String get(int index) {
		checkIndex(index);
		return args[index];
	}

	private void checkIndex(int index) {
		if (index < 0) {
			throw new CommandException("Internal command error. Tried to retrieve index: " + index);
		} else if (index >= args.length) {
			if (index < command.getUsageParams().length) {
				if (subCommand == null) {
					throw new CommandException("Missing argument: /" + command.getPrimaryAlias() + " " + command.getUsage());
				} else {
					throw new CommandException("Missing argument: /" + command.getPrimaryAlias() + " " + subCommand.getPrimaryAlias() + " " + subCommand.getUsage());
				}
			}
			//			throw new CommandException("Expected at least " + index + 1 + " arguments");
			throw CommandException.USAGE_ERROR;
		}
	}

	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	public void consumeArgument() {
		this.argLabel = args[0]; //Update arg label
		String[] temp = new String[args.length - 1];
		System.arraycopy(args, 1, temp, 0, temp.length);
		this.args = temp;
	}

	public boolean matches(int index, String... labels) {
		for (String label : labels) {
			if (get(index).equalsIgnoreCase(label)) {
				return true;
			}
		}
		return false;
	}

	public boolean startsWith(int index, String... labels) {
		for (String label : labels) {
			if (get(index).toLowerCase().startsWith(label.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(message);
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
