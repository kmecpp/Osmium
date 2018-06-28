package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public abstract class ComplexCommand extends Command {

	private ArrayList<CommandArg> args = new ArrayList<>();
	private ArrayList<CommandArg> publicArgs = new ArrayList<>();

	private String title = "Commands";
	private CS colors = CS.X6AB;
	private String shortest;

	public static final int ADMIN = 1;
	public static final int PLAYER = 2;

	public ComplexCommand(String name, String... aliases) {
		super(name, aliases);

		this.shortest = name;
		for (String alias : aliases) {
			if (alias.length() < shortest.length()) {
				shortest = alias;
			}
		}
	}

	public void onBaseCommand(CommandSender out) {
		ChatUtil.sendTitle(out, colors, title);
		for (CommandArg arg : getArgs(out)) {
			out.sendMessage(colors.getTertiary() + "/" + shortest + " " + arg.getLabel()
					+ (arg.getDescription().isEmpty()
							? " " + arg.getParams()
							: colors.getPrimary() + " - " + colors.getSecondary() + arg.getDescription()));
		}
	}

	@Override
	public final CommandResult onCommand(CommandSender out, String commandLabel, String[] args) {
		if (args.length == 0) {
			onBaseCommand(out);
			return CommandResult.SUCCESS;
		} else {
			//Verify correct argument length
			CommandArg matchingArg = null;
			for (CommandArg arg : getArgs(out)) { //Only use accessible commands
				if (arg.getLabel().equalsIgnoreCase(args[0])) {
					matchingArg = arg;
					String[] subArgs = sub(args, 1);
					if (!arg.isValidLength(subArgs)) {
						continue;
					}
					String result = arg.checkArgs(subArgs);
					return result.isEmpty()
							? onComplexCommand(out, args[0], subArgs)
							: CommandResult.USAGE_ERROR.message("Usage: /" + commandLabel + " " + result);
				}
			}
			return matchingArg != null
					? CommandResult.USAGE_ERROR.message("Usage: /" + commandLabel + " " + matchingArg.getLabel() + " " + matchingArg.getParams())
					: CommandResult.UNKNOWN_COMMAND;
		}
	}

	/**
	 * Called when a player executes a complex command with arguments
	 * 
	 * @param out
	 *            the sender of the command
	 * @param arg
	 *            the commandLabel of the complex command, not the first
	 *            argument
	 * @param args
	 *            the command arguments
	 * @return The result of the command
	 */
	public abstract CommandResult onComplexCommand(CommandSender out, String label, String[] args);

	public void registerArg(String command) {
		registerArg(command, 0, "");
	}

	public void registerArg(String command, int flags) {
		registerArg(command, flags, "");
	}

	public void registerArg(String command, String description) {
		registerArg(command, 0, description);
	}

	public void registerArg(String command, int flags, String description) {
		int index = command.indexOf(" ");
		CommandArg arg = new CommandArg(command.substring(0, index == -1 ? command.length() : index), index == -1 ? "" : command.substring(index + 1), flags, description);
		args.add(arg);
		if (!arg.isAdminOnly()) {
			publicArgs.add(arg);
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setColors(CS colors) {
		this.colors = colors;
	}

	public CS getColors() {
		return colors;
	}

	public ArrayList<CommandArg> getArgs(CommandSender sender) {
		return sender.isOp() ? args : publicArgs;
	}

	public ArrayList<CommandArg> getArgs() {
		return args;
	}

	public ArrayList<CommandArg> getPublicArgs() {
		return publicArgs;
	}

}
