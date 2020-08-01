package com.kmecpp.osmium.api.command;

import java.io.IOException;
import java.util.ArrayList;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.util.StringUtil;

public class Command extends CommandBase {

	private ArrayList<CommandBase> args = new ArrayList<>();

	private String title = "&a&l" + StringUtil.capitalize(getPrimaryAlias()) + " Commands";

	public Command() {
		this(null);
	}

	public Command(String name, String... aliases) {
		super(name, aliases);
		setPermission("osmium.commands." + Osmium.getPlugin(this.getClass()).getName().toLowerCase()); //Default permission
		configure();
	}

	public void configure() {
	}

	public void validate(CommandEvent event) {
		String[] usage = getUsageParams();
		if (event.size() < usage.length) {
			event.checkIndex(event.size());
		}
	}

	public void finalize(CommandEvent event) {
	}

	public void saveConfig(Class<?> config, CommandEvent event) {
		try {
			Osmium.getConfigManager().save(config);
		} catch (IOException e) {
			e.printStackTrace();
			event.sendMessage(Chat.RED + "Failed to save config file! Check console for details.");
		}
	}

	public final void setTitle(String title) {
		this.title = Chat.GREEN + Chat.BOLD.toString() + Chat.style(title);
	}

	public final void setRawTitle(String title) {
		this.title = title;
	}

	@Override
	public void execute(CommandEvent event) {
		if (getExecutor() != null) {
			super.execute(event);
		} else {
			event.send("");
			event.send(title);
			event.send("&e&m----------------------------------------");
			event.send("");
			for (CommandBase arg : args) {
				if (arg.isAllowed(event.getSender())) {
					event.send("&b/" + event.getBaseLabel() + " " + arg.getPrimaryAlias()
							+ (arg.hasUsage() ? " " + arg.getUsage() : "")
							+ (arg.hasDescription() ? "&e - &b" + arg.getDescription() : ""));
				}
			}
		}
	}

	public final CommandBase add(String name) {
		return add(name, new String[0]);
	}

	public final CommandBase add(String name, String... aliases) {
		CommandBase arg = new CommandBase(name, aliases);
		args.add(arg);
		return arg;
	}

	public CommandBase getArgumentMatching(String argLabel) {
		return getArgumentMatching(argLabel, null);
	}

	public CommandBase getArgumentMatching(String argLabel, CommandSender sender) {
		CommandBase highestMatch = null;
		int highestMatchCount = 0;
		for (CommandBase arg : args) {
			for (String alias : arg.getAliases()) {
				//Allow aliases partitioned with '/' to be treated as individual arguments
				for (String option : alias.split("/")) {
					if (argLabel.length() > option.length()) {
						continue;
					}

					int matching = 0;
					for (int i = 0; i < argLabel.length(); i++) {
						char c1 = Character.toLowerCase(argLabel.charAt(i));
						char c2 = Character.toLowerCase(option.charAt(i));
						if (c1 == c2) {
							matching++;
						} else {
							break;
						}
					}

					System.out.println("MATCH: " + alias + " = " + matching);
					if (matching == option.length()) {
						return arg;
					} else if (matching > highestMatchCount) {
						highestMatchCount = matching;
						highestMatch = arg;
					} else if (matching == highestMatchCount && highestMatch != arg) {
						highestMatch = null;
					}

					//					if (argLabel.toLowerCase().startsWith(input.toLowerCase())) {
					//						if (sender == null || !arg.isAllowed(sender)) {
					//							highestMatch = arg;
					//						} else {
					//							return arg;
					//						}
					//					}
				}
			}
		}

		return highestMatch; //Optional: if the highest match is not allowed, fallback to second highest? Probably not.
		//		throw new CommandException("Unknown command! Type /" + this.getPrimaryAlias() + " for a list of commands!");
	}

	//	public final void notFoundError(String type, String input) {
	//		throw new CommandException("&4Error: &c" + StringUtil.capitalize(type) + " not found: '" + input + "'");
	//	}

	public ArrayList<CommandBase> getArgs() {
		return args;
	}

	public final void fail(String message) {
		throw new CommandException(message);
	}

	public final void usageError() {
		throw CommandException.USAGE_ERROR;
	}

	public final void lacksPermission() {
		throw CommandException.LACKS_PERMISSION;
	}

}
