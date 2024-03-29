package com.kmecpp.osmium.api.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.util.StringUtil;

public class Command extends CommandBase {

	private ArrayList<CommandBase> args = new ArrayList<>();

	private String title = "&a&l" + StringUtil.capitalize(getPrimaryAlias()) + " Commands";
	private boolean hideDescriptions;
	private boolean nested;

	public Command() {
		this(null);
	}

	public Command(String name, String... aliases) {
		super(name, aliases);
		setPermission(getDefaultPermission()); //Default permission
		configure();
	}

	private String getDefaultPermission() {
		return "osmium.commands." + Osmium.getPlugin(this.getClass()).getName().toLowerCase();
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

	public void addHelpCommand() {
		addHelpCommand(true);
	}

	public void addHelpCommand(boolean hideDescriptions) {
		CommandBase helpArg = new CommandBase("help", "?").setUsage("{aliases}");
		helpArg.setDescription("Shows the descriptions for each subcommand");
		helpArg.setExecutor(e -> {
			e.sendTitle(title);

			boolean aliases = e.hasString(0) && e.getString(0).equalsIgnoreCase("aliases");
			String requestedCommand = e.getString(aliases ? 1 : 0, null);

			List<CommandBase> displayArgs;
			if (requestedCommand != null) {
				CommandBase arg = getArgumentMatching(requestedCommand, e.getSender());
				if (arg != null) {
					displayArgs = Arrays.asList(arg);
				} else {
					throw new CommandException("Unknown subcommand '" + requestedCommand + "'");
				}
			} else {
				displayArgs = args.subList(1, args.size());
			}

			String baseLabel = this.nested ? e.getBaseLabel() : this.getShortestAlias();

			if (aliases) {
				e.sendMessage(ChatColor.GREEN + "/" + baseLabel + " = " + Arrays.toString(this.getAliases()));
			}

			for (CommandBase arg : displayArgs) {
				if (arg.isAllowed(e.getSender())) {
					if (aliases) {
						e.sendMessage(ChatColor.GREEN + "/" + baseLabel + " " + arg.getPrimaryAlias() + " = " + Arrays.toString(arg.getAliases()));
					} else {
						e.sendMessage(ChatColor.GREEN + "/" + baseLabel + " " + arg.getPrimaryAlias() + ChatColor.AQUA + ": " + Chat.YELLOW + arg.getDescription());
					}

				}
			}
		});
		args.add(0, helpArg);

		this.hideDescriptions = hideDescriptions;
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
					event.sendMessage(Chat.AQUA + "/" + event.getBaseLabel() + " " + arg.getPrimaryAlias()
							+ (arg.hasUsage() ? " " + arg.getUsage() : "")
							+ (arg.hasDescription() && !hideDescriptions ? Chat.YELLOW + " - " + Chat.AQUA + arg.getDescription() : ""));
				}
			}
		}
	}

	public final void addSubCommand(Command command) {
		if (!command.hasDescription()) {
			command.setDescription("View " + command.getPrimaryAlias() + " commands");
		}
		if (command.getPermission().equals(command.getDefaultPermission())) {
			command.setPermission(this.getPermission()); //Inherit parent command's permission if sub command is not set
		}
		command.nested = true;
		args.add(command);
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

					if (!option.toLowerCase().startsWith(argLabel.toLowerCase())) {
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

					//					System.out.println("MATCH: " + alias + " = " + matching);
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
