package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.CommandSender;

public class CommandManager {

	private static ArrayList<Command> commands = new ArrayList<>();

	public static void register(Class<? extends Command> command) {
		commands.add(Reflection.newInstance(command));

		if (Platform.isBukkit()) {

		}
	}

	public static CommandBuilder register(String... aliases) {
		return new CommandBuilder(aliases);
	}

	public static class CommandBuilder {

		private String[] aliases;
		private String permission;
		private String description;

		public CommandBuilder(String[] aliases) {
			this.aliases = aliases;
		}

		public CommandBuilder permission(String permission) {
			this.permission = permission;
			return this;
		}

		public CommandBuilder description(String description) {
			this.description = description;
			return this;
		}

		public void executor(CommandExecutor executor) {
			commands.add(new Command() {

				@Override
				public void configure() {
					setAliases(aliases);
					setPermission(permission);
					setDescription(description);
				}

				@Override
				public void execute(CommandSender sender, String label, String[] args) {
					executor.execute(sender, label, args);
				}

			});
		}

	}

}
