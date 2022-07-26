package com.kmecpp.osmium.api.command;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.location.Location;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.StringUtil;

public class CommandEvent implements Messageable {

	private Command command;
	private CommandBase subCommand;
	private CommandSender sender;
	private String baseLabel;
	private String argLabel;
	private String[] args;

	private boolean cooldownActivated;
	//	private long cooldownOverride;

	public CommandEvent(Command command, CommandSender sender, String baseLabel, String argLabel, String[] args) {
		this.command = command;
		this.sender = sender;
		this.baseLabel = baseLabel;
		this.argLabel = argLabel;
		this.args = args;
	}

	public boolean isCooldownActivated() {
		return cooldownActivated;
	}

	public void setCooldownActivated(boolean cooldownActivated) {
		this.cooldownActivated = true;
	}

	//TODO: This is better. Just needs execution time + cooldown time
	//	public void setCooldownActivated(boolean cooldownActivated, long cooldown) {
	//		setCooldownActivated(cooldownActivated, cooldown, MilliTimeUnit.MILLISECOND);
	//	}
	//
	//	public void setCooldownActivated(boolean cooldownActivated, long cooldown, MilliTimeUnit unit) {
	//		this.cooldownActivated = cooldownActivated;
	//		this.cooldownOverride = cooldown * unit.getMillisecondTime();
	//	}
	//
	//	public long getCooldownOverride() {
	//		return cooldownOverride;
	//	}

	public Command getCommand() {
		return command;
	}

	public boolean isPlayer() {
		return sender instanceof Player;
	}

	public Player getPlayer() {
		if (sender instanceof Player) {
			return (Player) sender;
		} else {
			throw CommandException.PLAYERS_ONLY;
		}
	}

	public boolean isOp() {
		return sender.isOp();
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

	public Optional<CommandBase> getSubCommand() {
		return Optional.ofNullable(subCommand);
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

	public boolean hasFlag(String... flags) {
		for (String arg : args) {
			if (arg.startsWith("-")) {
				for (String flag : flags) {
					if (arg.substring(1).equalsIgnoreCase(flag)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isInt(int index) {
		if (index >= 0 && index < args.length) {
			try {
				Integer.parseInt(get(index));
				return true;
			} catch (NumberFormatException ex) {}
		}
		return false;
	}

	public int getInt(int index) {
		String input = get(index);
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			if (StringUtil.isMathematicalInteger(input)) {
				throw new CommandException("Expected an integer but given value was too large: '" + input + "'");
			} else {
				if (index >= 0 && index < command.getUsageParams().length) {
					throw new CommandException("Expected an integer " + Chat.DARK_RED + "<" + command.getUsageParams()[index] + ">" + Chat.RED + " received: '" + input + "'");
				} else {
					throw new CommandException("Expected an integer received: '" + input + "'");
				}
			}
		}
	}

	public int getInt(int index, int def) {
		return index < args.length ? getInt(index) : def;
	}

	public int getPositiveInt(int index) {
		int result = getInt(index);
		if (result < 1) {
			throw new CommandException("Input '" + result + "' must be at least 1");
		}
		return result;
	}

	public int getPositiveInt(int index, int defaultValue) {
		int result = getInt(index, defaultValue);
		if (result < 1) {
			throw new CommandException("Input '" + result + "' must be at least 1");
		}
		return result;
	}

	public int getIntMax(int index, int max) {
		int result = getInt(index);
		if (result > max) {
			throw new CommandException("Input '" + result + "' is too large. Max value: " + max);
		}
		return result;
	}

	public int getIntMin(int index, int min) {
		int result = getInt(index);
		if (result < min) {
			throw new CommandException("Input '" + result + "' is too small. Min value: " + min);
		}
		return result;
	}

	public int getIntBound(int index, int min, int max) {
		int result = getInt(index);
		if (result < min || result > max) {
			throw new CommandException(getInputReference(index, result) + " must be between " + min + " and " + max);
		}
		return result;
	}

	public int getIntBound(int index, int min, int max, String name) {
		int result = getInt(index);
		if (result < min || result > max) {
			throw new CommandException("Input '" + name + "' was " + result + " but must be between " + min + " and " + max);
		}
		return result;
	}

	public boolean isLong(int index) {
		if (index >= 0 && index < args.length) {
			try {
				Long.parseLong(get(index));
				return true;
			} catch (NumberFormatException ex) {}
		}
		return false;
	}

	public long getLong(int index) {
		String input = get(index);
		try {
			return Long.parseLong(input);
		} catch (NumberFormatException e) {
			if (StringUtil.isMathematicalInteger(input)) {
				throw new CommandException("Expected an integer but given value was too large: '" + input + "'");
			} else {
				if (index >= 0 && index < command.getUsageParams().length) {
					throw new CommandException("Expected an integer " + Chat.DARK_RED + "<" + command.getUsageParams()[index] + ">" + Chat.RED + " received: '" + input + "'");
				} else {
					throw new CommandException("Expected an integer received: '" + input + "'");
				}
			}
		}
	}

	public long getLong(int index, long defaultValue) {
		return index < args.length ? getLong(index) : defaultValue;
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
		if (StringUtil.startsWithIgnoreCase(input, "true", "1", "yes", "enable")) {
			return true;
		} else if (StringUtil.startsWithIgnoreCase(input, "false", "0", "no", "disable")) {
			return false;
		} else {
			throw new CommandException("Expected a boolean but got: '" + input + "'");
		}
	}

	public boolean getBoolean(int index, boolean def) {
		return index < args.length ? getBoolean(index) : def;
	}

	public boolean hasString(int index) {
		return index >= 0 && index < args.length;
	}

	public boolean getBoolean(int index, String trueString, String falseString) {
		return getBoolean(getString(index), trueString, falseString);
	}

	public boolean getBoolean(String input, String trueString, String falseString) {
		if (input.equalsIgnoreCase(trueString)) {
			return true;
		} else if (input.equalsIgnoreCase(falseString)) {
			return false;
		} else {
			throw new CommandException("Expected '" + trueString + "' or '" + falseString + "'. Got: '" + input + "'");
		}
	}

	public String getString(int index) {
		return get(index);
	}

	public String getStringAlpha(int index) {
		return getStringAlpha(index, -1);
	}

	public String getStringAlpha(int index, int maxLength) {
		String result = getString(index);
		if (!StringUtil.isAlpha(result)) {
			throw new CommandException("Input '" + result + "' contains invalid characters");
		} else if (maxLength >= 0 && result.length() > maxLength) {
			throw new CommandException("Input '" + result + "' is " + (result.length() - maxLength) + " characters too long");
		}
		return result;
	}

	public String getStringAlphaNumeric(int index) {
		return getStringAlphaNumeric(index, -1);
	}

	public String getStringAlphaNumeric(int index, int maxLength) {
		String result = getString(index);
		if (!StringUtil.isAlphaNumeric(result)) {
			throw new CommandException("Input '" + result + "' contains invalid characters");
		} else if (maxLength >= 0 && result.length() > maxLength) {
			throw new CommandException("Input '" + result + "' is " + (result.length() - maxLength) + " characters too long");
		}
		return result;
	}

	public String getStringMaxLength(int index, int maxLength) {
		String result = get(index);
		if (result.length() > maxLength) {
			throw new CommandException("Input '" + result + "' is " + (result.length() - maxLength) + " characters too long");
		}
		return result;
	}

	public String getString(int index, String def) {
		return index < args.length ? getString(index) : def;
	}

	public boolean contains(String parameter) {
		return contains(0, parameter);
	}

	public boolean contains(int startIndex, String parameter) {
		for (int i = startIndex; i < args.length; i++) {
			if (args[i].equalsIgnoreCase(parameter)) {
				return true;
			}
		}
		return false;
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

	public UUID getId(int index) {
		String input = getString(index);
		try {
			return UUID.fromString(input);
		} catch (IllegalArgumentException e) {
			throw new CommandException("Invalid UUID: " + input);
		}
	}

	public UUID getId(int index, UUID def) {
		return index < args.length ? getId(index) : def;
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
		if (args.length == index) {
			return "";
		}
		checkIndex(index);
		return String.join(" ", Arrays.copyOfRange(args, index, args.length));
	}

	public String get(int index) {
		checkIndex(index);
		return args[index];
	}

	public void checkIndex(int index) {
		if (index < 0) {
			throw new CommandException("Internal command error. Tried to retrieve index: " + index);
		} else if (index >= args.length) {
			CommandBase target = subCommand == null ? command : subCommand;

			if (index < target.getUsageParams().length) {
				throw new CommandException("Missing " + StringUtil.nth(index + 1) + " argument"
						+ (target.getUsage().isEmpty() ? "!"
								: (": /" + this.baseLabel + " " + (subCommand != null ? this.argLabel + " " : "") + target.getUsageHighlight(index))));
			}
			//			throw new CommandException("Expected at least " + index + 1 + " arguments");
			throw CommandException.USAGE_ERROR;
		}
	}

	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	void consumeArgument(CommandBase subCommand) {
		this.argLabel = args[0]; //Update arg label
		String[] temp = new String[args.length - 1];
		System.arraycopy(args, 1, temp, 0, temp.length);
		this.args = temp;

		this.subCommand = subCommand;
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

	public <T> T getOptional(Optional<T> optional, String message) {
		if (!optional.isPresent()) {
			error(message);
		}
		return optional.get();
	}

	public <T> T getNonNull(T obj, String message) {
		if (obj == null) {
			error(message);
		}
		return obj;
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(message);
	}

	public void handleError(Throwable t) {
		t.printStackTrace();
		send("&cError: " + t.getMessage());
	}

	public void error(String message) {
		throw new CommandException(message);
	}

	public void fail(String message) {
		throw new CommandException(message);
	}

	public void usageError() {
		throw CommandException.USAGE_ERROR;
	}

	public CommandException notFound(String expectedLabel, String input) {
		return new CommandException("Could not find " + expectedLabel + ": '" + input + "'");
	}

	//	public void sendRawMessage(String style, String message) {
	//		sender.sendMessage(style, message);
	//	}

	private String getInputReference(int index, int providedValue) {
		CommandBase target = subCommand == null ? command : subCommand;
		String usageParameter = target.getUsageParameter(index);
		return usageParameter != null ? ("Value for <" + usageParameter + "> (" + providedValue + ")") : ("Input '" + providedValue + "'");
	}

}
