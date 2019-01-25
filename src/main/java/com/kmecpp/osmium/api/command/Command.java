package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

import com.kmecpp.osmium.api.util.StringUtil;

public class Command extends SimpleCommand {

	private ArrayList<SimpleCommand> args = new ArrayList<>();

	private String title = "&a&l" + StringUtil.capitalize(getPrimaryAlias()) + " Commands";

	public Command() {
		this(null);
	}

	public Command(String name, String... aliases) {
		super(name, aliases);
		configure();
		//		this.properties = new CommandProperties(this.getClass().getAnnotation(Command.class));
	}

	public void configure() {
	}

	public void validate() {
	}

	public void execute(CommandEvent event) {
		//		if (args.isEmpty()) {
		//			this.execute(event);
		//		} else {
		//			if (event.getArgs().length == 0) {
		//				sendHelp(event);
		//			} else {
		//				String argLabel = event.getArg(0);
		//				for (SimpleCommand arg : args) {
		//					for (String alias : arg.getAliases()) {
		//						if (alias.equalsIgnoreCase(argLabel)) {
		//							arg.checkPermission(event);
		//							event.consumeArgument();
		//							arg.execute(event);
		//							return;
		//						}
		//					}
		//				}
		//				throw new CommandException("Unknown command! Type /" + this.getPrimaryAlias() + " for a list of commands!");
		//			}
		//		}
	}

	public final void setTitle(String title) {
		this.title = Chat.style(title);
	}

	public final void setRawTitle(String title) {
		this.title = title;
	}

	public void sendHelp(CommandEvent event) {
		event.send("");
		event.send(title);
		event.send("&e&m----------------------------------------");
		event.send("");
		for (SimpleCommand arg : args) {
			if (arg.isAllowed(event.getSender())) {
				event.send("&b/" + this.getPrimaryAlias() + " " + arg.getPrimaryAlias()
						+ (arg.hasUsage() ? " " + arg.getUsage() : "")
						+ (arg.hasDescription() ? "&e - &b" + arg.getDescription() : ""));
			}
		}
	}

	public final SimpleCommand add(String name, String... aliases) {
		SimpleCommand arg = new SimpleCommand(name, aliases);
		args.add(arg);
		return arg;
	}

	public SimpleCommand getArgumentMatching(String argLabel) {
		for (SimpleCommand arg : args) {
			for (String alias : arg.getAliases()) {
				//Allow aliases partitioned with '/' to be treated as individual arguments
				for (String part : alias.split("/")) {
					if (part.equalsIgnoreCase(argLabel)) {
						return arg;
					}
				}
			}
		}
		throw new CommandException("Unknown command! Type /" + this.getPrimaryAlias() + " for a list of commands!");
	}

	//	public final void notFoundError(String type, String input) {
	//		throw new CommandException("&4Error: &c" + StringUtil.capitalize(type) + " not found: '" + input + "'");
	//	}

	public ArrayList<SimpleCommand> getArgs() {
		return args;
	}

	public final void usageError() {
		throw CommandException.USAGE_ERROR;
	}

	public final void lacksPermission() {
		throw CommandException.LACKS_PERMISSION;
	}

}
