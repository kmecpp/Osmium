package com.kmecpp.osmium.api.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.BungeeAccess;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.TimeUtil;
import com.kmecpp.osmium.core.OsmiumCoreCommands;
import com.kmecpp.osmium.platform.osmium.CommandRedirectSender;

public final class CommandManager {

	//	private HashMap<OsmiumPlugin, Boolean> defaultAllowConsole = new HashMap<>();
	private HashMap<OsmiumPlugin, ArrayList<Command>> commands = new HashMap<>();
	private HashMap<Class<?>, Command> commandClassMap = new HashMap<>();

	private HashMap<UUID, HashMap<CommandBase, Long>> cooldownData = new HashMap<>();

	static {
		initializeDefaultExceptions();
	}

	public Command register(OsmiumPlugin plugin, String name, String... aliases) {
		return register(plugin, new Command(name, aliases));
	}

	public Command register(OsmiumPlugin plugin, Command command) {
		commands.computeIfAbsent(plugin, k -> new ArrayList<>()).add(command);
		commandClassMap.put(command.getClass(), command);

		plugin.debug("Registered command: /" + command.getPrimaryAlias());
		if (Platform.isBukkit()) {
			BukkitAccess.registerCommand(plugin, command);
		} else if (Platform.isSponge()) {
			SpongeAccess.registerCommand(plugin, command);
		} else if (Platform.isBungeeCord()) {
			BungeeAccess.registerCommand(plugin, command);
		}
		return command;
	}

	//	public void setAllowConsoleByDefault(OsmiumPlugin plugin, boolean allow) {
	//		defaultAllowConsole.put(plugin, allow);
	//	}
	//
	//	public boolean isConsoleAllowedByDefault(OsmiumPlugin plugin) {
	//		return defaultAllowConsole.getOrDefault(plugin, true);
	//	}

	public Command getCommand(Class<? extends Command> commandClass) {
		return commandClassMap.get(commandClass);
	}

	public Map<OsmiumPlugin, ArrayList<Command>> getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	public List<Command> getCommands(OsmiumPlugin plugin) {
		return Collections.unmodifiableList(commands.getOrDefault(plugin, new ArrayList<>()));
	}

	public HashMap<UUID, HashMap<CommandBase, Long>> getCooldownData() {
		return cooldownData;
	}

	public void processConsoleCommand(String command) {
		OsmiumLogger.info("Programmatically processing console command: " + command);
		if (Platform.isBukkit()) {
			BukkitAccess.processConsoleCommand(command);
		} else if (Platform.isSponge()) {
			SpongeAccess.processConsoleCommand(command);
		} else if (Platform.isBungeeCord()) {
			BungeeAccess.processConsoleCommand(command);
		}
	}

	public void processConsoleCommand(CommandSender outputReceiver, String command) {
		OsmiumLogger.info("Programmatically processing and capturing output for console command: " + command);
		if (Platform.isBukkit()) {
			BukkitAccess.processConsoleCommand(outputReceiver, command);
		} else if (Platform.isSponge()) {
			SpongeAccess.processConsoleCommand(outputReceiver, command);
		} else if (Platform.isSponge()) {
			BungeeAccess.processConsoleCommand(outputReceiver, command);
		}
	}

	public void processCommand(CommandSender sender, String command) {
		OsmiumLogger.info("Programmatically processing command for " + sender.getName() + ": " + command);
		if (Platform.isBukkit()) {
			BukkitAccess.processCommand((org.bukkit.command.CommandSender) sender.getSource(), command);
		} else if (Platform.isSponge()) {
			SpongeAccess.processCommand((org.spongepowered.api.command.CommandSource) sender.getSource(), command);
		} else if (Platform.isBungeeCord()) {
			BungeeAccess.processCommand((net.md_5.bungee.api.CommandSender) sender.getSource(), command);
		}
	}

	public void processCommand(CommandSender sender, CommandSender receiver, String command) {
		processCommand(new CommandRedirectSender(sender, receiver), command);
	}

	public boolean invokeCommand(Command command, CommandSender sender, String commandLabel, String[] args) {
		try {
			//			if (sender instanceof ConsoleCommandSender && !command.isConsole()) {
			//				throw CommandException.PLAYERS_ONLY;
			//			}

			CommandEvent event = new CommandEvent(command, sender, commandLabel, "", args);
			command.checkPermission(event);

			//Simple commands
			if (command.getArgs().isEmpty()) {
				if (OsmiumCoreCommands.processAliasRequest(event)) {
					return true;
				}

				command.validate(event);
				tryExecuteCommand(sender, command, event);
				command.finalize(event);
			}

			//Commands with registered arguments
			else {
				if (args.length == 0) {
					command.execute(event);
				} else {
					CommandBase arg = command.getArgumentMatching(args[0]);
					if (arg == null) {
						sender.sendMessage(Chat.RED + "Unknown command! Type " + Chat.YELLOW + "/" + commandLabel + Chat.RED + " for a list of commands.");
						return false;
					} else if (!arg.isAllowed(sender)) {
						sender.sendMessage(Chat.RED + "You do not have permission to perform this command!");
						return false;
					} else if (arg instanceof Command) {
						event.consumeArgument(arg);
						invokeCommand((Command) arg, sender, commandLabel + " " + event.getArgLabel(), event.getArgs());
					} else {
						event.consumeArgument(arg);

						if (OsmiumCoreCommands.processAliasRequest(event)) {
							return true;
						}

						command.validate(event);
						tryExecuteCommand(sender, arg, event);
						command.finalize(event);
						return true;
					}
				}
			}
			return true;
		} catch (CommandException e) {
			//			e.printStackTrace(); //WARNING: THIS IS USELESS FOR DEFAULT EXCEPTIONS
			if (e == CommandException.USAGE_ERROR) {
				if (args.length > 0) {
					CommandBase arg = command.getArgumentMatching(args[0]);
					if (arg != null) {
						sender.sendMessage(Chat.RED + "Usage: /" + commandLabel + " " + arg.getPrimaryAlias() + " " + arg.getUsage());
						return false;
					}
				}
				sender.send("&cUsage: /" + commandLabel + " " + command.getUsage());
			} else {
				sender.send("&c" + e.getMessage());
			}
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(Chat.RED + "Internal command error. Please see console for details");
			e.printStackTrace();
			return false;
		} catch (Throwable t) {
			sender.sendMessage(Chat.RED + t.getMessage());
			t.printStackTrace();
			return false;
		}
	}

	private void tryExecuteCommand(CommandSender sender, CommandBase command, CommandEvent event) {
		if (sender instanceof Player) {
			long currentTime = System.currentTimeMillis();

			boolean cooldownBypass = sender.hasPermission("osmium.commandcooldown.bypass") || sender.hasPermission("osmium.commandcooldown.bypass." + command.getPrimaryAlias());

			if (!cooldownBypass) {
				HashMap<CommandBase, Long> playerCommandCooldowns = cooldownData.get(((Player) sender).getUniqueId());
				if (playerCommandCooldowns != null) {
					Long lastUsed = playerCommandCooldowns.get(command);
					if (lastUsed != null) {
						long timePassed = currentTime - lastUsed;
						if (timePassed < command.getCooldown()) {
							System.out.println("REMAINING: " + (command.getCooldown() - timePassed));
							throw new CommandException("You cannot use that command for another " + TimeUtil.formatTotalMillis(command.getCooldown() - timePassed));
						}
					}
				}
			}
			command.execute(event);
			if (!cooldownBypass && event.isCooldownActivated()) {
				Player player = (Player) sender;
				cooldownData.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(command, currentTime);
			}
		} else {
			command.execute(event);
		}
	}

	public static void sendFailedRegistrationMessage(OsmiumPlugin plugin, CommandBase command) {
		OsmiumLogger.warn("Unable to register /" + command.getPrimaryAlias() + " for plugin: " + plugin.getName() + " because its aliases are unavailable!");
		OsmiumLogger.warn("To correct this issue, create a command rewrite rule in the osmium config");
		OsmiumLogger.warn("commands." + command.getPrimaryAlias() + "={alternate}");
	}

	private static void initializeDefaultExceptions() {
		new CommandException("");
	}

	//	private static ArrayList<Command> commands = new ArrayList<>();
	//
	//	public CommandManager register(Class<? extends OsmiumCommand> command) {
	//		//		commands.add(Reflection.newInstance(command));
	//
	//		if (Platform.isBukkit()) {
	//
	//		} else if (Platform.isSponge()) {
	//
	//		}
	//		return this;
	//	}
	//
	//	public static CommandBuilder register(String... aliases) {
	//		return new CommandBuilder(aliases);
	//	}
	//
	//	public static class CommandBuilder {
	//
	//		private String[] aliases;
	//		private String permission;
	//		private String description;
	//
	//		public CommandBuilder(String[] aliases) {
	//			this.aliases = aliases;
	//		}
	//
	//		public CommandBuilder permission(String permission) {
	//			this.permission = permission;
	//			return this;
	//		}
	//
	//		public CommandBuilder description(String description) {
	//			this.description = description;
	//			return this;
	//		}
	//
	//		public void executor(CommandExecutor executor) {
	//			//			commands.add(new OsmiumCommand() {
	//			//
	//			//				@Override
	//			//				public void configure() {
	//			//					setAliases(aliases);
	//			//					setPermission(permission);
	//			//					setDescription(description);
	//			//				}
	//			//
	//			//				@Override
	//			//				public void execute(CommandSender sender, String label, String[] args) {
	//			//					executor.execute(sender, label, args);
	//			//				}
	//			//
	//			//			});
	//		}
	//
	//	}

	//	private static ArrayList<CommandBase> commands = new ArrayList<>();
	//	private static HashSet<String> overrides = new HashSet<>();
	//
	//	//	public static void registerCommand(String commandLabel, String[] aliases, Class<? extends Command> executor) {
	//	//		try {
	//	//			Command command = executor.newInstance();
	//	//			commands.add(command);
	//	//			command.registerCommand(commandLabel, aliases);
	//	//		} catch (Exception e) {
	//	//			e.printStackTrace();
	//	//		}
	//	//	}
	//
	//	public static void runCommand(CommandSender out, String commandStr) {
	//		String[] parts = commandStr.split(" ");
	//		if (parts.length > 0) {
	//			String label = parts[0];
	//			for (CommandBase command : commands) {
	//				for (String alias : command.getAliases()) {
	//					if (label.equalsIgnoreCase(alias)) {
	//						command.execute(out, label, Arrays.copyOfRange(parts, 1, parts.length));
	//					}
	//				}
	//			}
	//		}
	//	}
	//
	//	public static void registerOverrides(String... commands) {
	//		for (String command : commands) {
	//			overrides.add(command.toLowerCase());
	//		}
	//	}
	//
	//	public static boolean isOverriden(String command) {
	//		return overrides.contains(command.toLowerCase());
	//	}
	//
	//	/**
	//	 * Called to register the command directly into the main server command map
	//	 */
	//	public static final void register(Class<? extends CommandBase> commandClass) {
	//		//Code for getting current version
	//		//String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	//		try {
	//			CommandBase command = commandClass.newInstance();
	//			commands.add(command);
	//
	//			SimpleCommandMap commandMap = (SimpleCommandMap) Reflection.getFieldValue(Bukkit.getServer(), "commandMap");
	//			//			for (String alias : command.getAliases()) {
	//			//				for (org.bukkit.command.Command c : commandMap.getCommands()) {
	//			//					if (c.getAliases().contains(alias)) {
	//			//						CoreLogger.info("Unregistering command alias: " + alias);
	//			//						c.getAliases().remove(alias);
	//			//					}
	//			//				}
	//			//			}
	//
	//			commandMap.register(command.getName(), command.getCommand());
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	public static ArrayList<CommandBase> getCommands() {
	//		return commands;
	//	}

}
