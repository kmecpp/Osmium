package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

import com.kmecpp.osmium.util.StringUtil;

public class Command extends SimpleCommand {

	private ArrayList<SimpleCommand> args = new ArrayList<>();

	private String title = "&a&l" + StringUtil.capitalize(getPrimaryAlias()) + " Commands";

	public Command(String... aliases) {
		super(null);
		configure();
		//		this.properties = new CommandProperties(this.getClass().getAnnotation(Command.class));
	}

	public void configure() {
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public void execute(CommandEvent e) {
		if (args.isEmpty()) {
			this.execute(e);
		} else {
			if (e.getArgs().length == 0) {
				e.sendMessage("");
				e.sendMessage(title);
				e.sendMessage("&e&m----------------------------------------");
				e.sendMessage("");
				for (SimpleCommand arg : args) {
					e.sendMessage("&b/" + this.getPrimaryAlias() + " " + arg.getPrimaryAlias()
							+ (arg.hasUsage() ? " " + arg.getUsage() : "")
							+ (arg.hasDescription() ? "&e - &b" + arg.getDescription() : ""));
					//					+ " " + arg.getPrimaryAlias() + (arg.getDescription().isEmpty() ? "" : "&e - &b" + arg.getDescription()));
				}
			} else {
				String argLabel = e.getArg(0);
				for (SimpleCommand arg : args) {
					for (String alias : arg.getAliases()) {
						if (alias.equalsIgnoreCase(argLabel)) {
							e.consumeArgument();
							arg.execute(e);
							return;
						}
					}
				}
				throw new CommandException("Unknown command! Type /" + this.getPrimaryAlias() + " for a list of commands!");
			}
		}
	}

	public final SimpleCommand add(String name, String... aliases) {
		SimpleCommand arg = new SimpleCommand(name, aliases);
		args.add(arg);
		return arg;
	}

	public final void setArg(String label, CommandExecutor executor) {
		setArg(label, "", "", executor);
	}

	public final void setArg(String label, String usage, String description, CommandExecutor executor) {

	}

	//	public final void notFoundError(String type, String input) {
	//		throw new CommandException("&4Error: &c" + StringUtil.capitalize(type) + " not found: '" + input + "'");
	//	}

	public final CommandException usageError() {
		return CommandException.USAGE_ERROR;
	}

}
