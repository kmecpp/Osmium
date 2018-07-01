package com.kmecpp.osmium.api.command;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.kmecpp.jlib.utils.StringUtil;

public abstract class CommandBase {

	private BukkitCommand command;
	private String[] aliases;

	private boolean opsOnly;

	public CommandBase(String name, String... aliases) {
		this.aliases = new String[aliases.length + 1];
		this.aliases[0] = name;
		System.arraycopy(aliases, 0, this.aliases, 1, aliases.length);

		this.command = new BukkitCommand(name, "", "", Arrays.asList(aliases)) { //Usage message cannot be null or else stuff will break

			@Override
			public boolean execute(CommandSender sender, String label, String[] args) {
				return CommandBase.this.execute(sender, label, args);
			}

		};
	}

	protected BukkitCommand getCommand() {
		return command;
	}

	public String getName() {
		return command.getLabel();
	}

	public String[] getAliases() {
		return aliases;
	}

	public String getDescription() {
		return command.getDescription();
	}

	public String getUsage() {
		return command.getUsage();
	}

	protected void setOpsOnly() {
		opsOnly = true;
	}

	protected void setPermission(String permission) {
		command.setPermission(permission);
	}

	protected void setUsage(String usage) {
		command.setUsage(usage);
	}

	protected boolean hasPermission(Permissible permissible) {
		return command.getPermission() == null ? true : permissible.hasPermission(command.getPermission());
	}

	/**
	 * Attempts to casts the command sender to a player. If this operation is
	 * successful, the player object is returned. If it is not, a
	 * CommandException will be thrown exiting the command logic.
	 * 
	 * @param sender
	 *            the sender to cast
	 * @return the Player representation of the command sender
	 */
	public Player getPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return (Player) sender;
		}
		throw new CommandException(CommandResult.PLAYER_ONLY);
	}

	/**
	 * Enforces operator status on the sender of the current command by throwing
	 * a {@link CommandException} and exiting the command logic of the sender
	 * lacks the required permissions
	 * 
	 * @param sender
	 *            the sender to filter for operator status
	 */
	public void filterOps(CommandSender sender) {
		if (!sender.isOp()) {
			throw new CommandException(CommandResult.LACKS_PERMISSION);
		}
	}

	public abstract CommandResult onCommand(CommandSender out, String label, String[] args);

	public final boolean execute(CommandSender out, String label, String[] args) {
		CommandResult result = null;

		if ((opsOnly && !out.isOp()) || !hasPermission(out)) {
			result = CommandResult.LACKS_PERMISSION;
		} else {
			try {
				result = onCommand(out, label, args); //Run the implementation and get the result
			} catch (CommandException exception) {
				result = exception.getResult();
			}
		}

		String commandLabel = (command.getAliases().size() == 0 ? command.getLabel() : command.getAliases().get(0));//Get the preferred label for output messages
		String message = result.extractMessage();
		if (message == null) {//Check if there is a specified message 
			//Run defaults
			switch (result) {
			case SUCCESS:
				//Command executed successfully
				break;
			case ERROR:
				break;
			case USAGE_ERROR:
				out.sendMessage(ChatColor.RED + (command.getUsage().equals("")
						? ("Incorrect usage!" + (this instanceof ComplexCommand ? " Type /" + commandLabel + " for a list of commands!" : " There is no help message for this command."))
						: ChatColor.RED + "Usage: " + command.getUsage()));
				break;
			case LACKS_PERMISSION:
				out.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
				break;
			case UNKNOWN_COMMAND:
				out.sendMessage(ChatColor.RED + "Unknown command! Type /" + commandLabel + " for a list of commands!");
				break;
			case PLAYER_NOT_FOUND:
				out.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Player not found!");
				break;
			case PLAYER_ONLY:
				out.sendMessage(ChatColor.RED + "Only in game players can use this command!");
				break;
			}
		} else {
			out.sendMessage((result.boolValue() ? ChatColor.GREEN : ChatColor.RED) + message); //Run specified message
		}
		return result.boolValue();
	}

	public static void maxArgs(String[] args, int max, String message) {
		if (args.length > max) {
			throw new CommandException(CommandResult.USAGE_ERROR.message(message));
		}
	}

	public static void minArgs(String[] args, int min, String message) {
		if (args.length < min) {
			throw new CommandException(CommandResult.USAGE_ERROR.message(message));
		}
	}

	public static String[] sub(String[] args, int start) {
		if (start >= args.length) {
			return new String[0];
		}
		String[] sub = new String[args.length - start];
		System.arraycopy(args, start, sub, 0, sub.length);
		return sub;
	}

	public static String substring(String[] args, int start) {
		return StringUtil.join(sub(args, start), " ");
	}

	public int getInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new CommandException(CommandResult.ERROR.message("Expected integer argument! Found: '" + str + "'"));
		}
	}

	//	/**
	//	 * Called to register the command directly into the main server command map
	//	 */
	//	public final void registerCommand(String name, String[] aliases) {
	//		if (command == null) {
	//			CoreLogger.warning("Cannot register command without aliases: '" + this.getClass().getName() + "'");
	//			return;
	//		}
	//
	//		//Code for getting current version
	//		//String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	//		try {
	//			Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
	//
	//			bukkitCommandMap.setAccessible(true);
	//			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
	//			commandMap.register(name, command);
	//		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
	//			e.printStackTrace();
	//		}
	//
	//		//Remove NMS version dependent code
	//		//((CraftServer) Bukkit.getServer()).getCommandMap().register(this.getName(), this);
	//	}

}
