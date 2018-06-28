package com.kmecpp.osmium.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import com.kmecpp.jlib.reflection.Reflection;

public final class CommandManager {

	private static ArrayList<Command> commands = new ArrayList<>();
	private static HashSet<String> overrides = new HashSet<>();

	//	public static void registerCommand(String commandLabel, String[] aliases, Class<? extends Command> executor) {
	//		try {
	//			Command command = executor.newInstance();
	//			commands.add(command);
	//			command.registerCommand(commandLabel, aliases);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}

	public static void runCommand(CommandSender out, String commandStr) {
		String[] parts = commandStr.split(" ");
		if (parts.length > 0) {
			String label = parts[0];
			for (Command command : commands) {
				for (String alias : command.getAliases()) {
					if (label.equalsIgnoreCase(alias)) {
						command.execute(out, label, Arrays.copyOfRange(parts, 1, parts.length));
					}
				}
			}
		}
	}

	public static void registerOverrides(String... commands) {
		for (String command : commands) {
			overrides.add(command.toLowerCase());
		}
	}

	public static boolean isOverriden(String command) {
		return overrides.contains(command.toLowerCase());
	}

	/**
	 * Called to register the command directly into the main server command map
	 */
	public static final void register(Class<? extends Command> commandClass) {
		//Code for getting current version
		//String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		try {
			Command command = commandClass.newInstance();
			commands.add(command);

			SimpleCommandMap commandMap = (SimpleCommandMap) Reflection.getFieldValue(Bukkit.getServer(), "commandMap");
			//			for (String alias : command.getAliases()) {
			//				for (org.bukkit.command.Command c : commandMap.getCommands()) {
			//					if (c.getAliases().contains(alias)) {
			//						CoreLogger.info("Unregistering command alias: " + alias);
			//						c.getAliases().remove(alias);
			//					}
			//				}
			//			}

			commandMap.register(command.getName(), command.getCommand());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Command> getCommands() {
		return commands;
	}

}
