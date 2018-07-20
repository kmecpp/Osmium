package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

import com.kmecpp.jlib.utils.StringUtil;
import com.kmecpp.osmium.Log;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class Command extends SimpleCommand {

	private ArrayList<SimpleCommand> args = new ArrayList<>();

	private String title = "&a&l" + StringUtil.capitalize(getPrimaryAlias()) + " Commands";

	public Command(String... aliases) {
		super(aliases);
		configure();
		//		this.properties = new CommandProperties(this.getClass().getAnnotation(Command.class));
	}

	public void configure() {
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public void execute(CommandEvent e) {
		System.out.println("Execute: " + e);
		long start = System.nanoTime();
		for (int i = 0; i < 10; i++) {
			Log.info("Hey");
		}
		long end = System.nanoTime();
		System.out.println("TIME: " + ((end - start) / 1000F) + "us");

		start = System.nanoTime();
		for (int i = 0; i < 10; i++) {
			OsmiumLogger.info("Hey");
		}
		end = System.nanoTime();
		System.out.println("TIME: " + ((end - start) / 1000F) + "us");

		if (!args.isEmpty()) {
			if (e.getArgs().length == 0) {
				e.sendMessage("");
				e.sendMessage(title);
				e.sendMessage("&e&m----------------------------------------");
				e.sendMessage("");
				for (SimpleCommand arg : args) {
					e.sendMessage("&b/" + arg.getPrimaryAlias()
							+ (arg.getDescription().isEmpty() ? "" : "&e - &b" + arg.getDescription()));
				}
			} else {
				String arg = e.getArg(1);
				for (SimpleCommand a : args) {
					for (String alias : a.getAliases()) {
						if (alias.equalsIgnoreCase(arg)) {
							//							a.execute();
						}
					}
				}
			}
		}
	}

	public final SimpleCommand add(String... aliases) {
		return new SimpleCommand(aliases);
	}

	public final void setArg(String label, CommandExecutor executor) {
		setArg(label, "", "", executor);
	}

	public final void setArg(String label, String usage, String description, CommandExecutor executor) {

	}

	public final void notFoundError(String type, String input) {
		throw new CommandException("&4Error: &c" + StringUtil.capitalize(type) + " not found: '" + input + "'");
	}

	public final void usageError() {
		throw CommandException.USAGE_ERROR;
	}

}
