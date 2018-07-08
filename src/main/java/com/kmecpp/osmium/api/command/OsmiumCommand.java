package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

public abstract class OsmiumCommand {

	private CommandProperties properties;
	private ArrayList<CommandProperties> args = new ArrayList<>();

	private String title = "&a&lCommand List";

	public OsmiumCommand() {
		this.properties = new CommandProperties(this.getClass().getAnnotation(Command.class));
	}

	public void configure() {
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public void execute(CommandEvent e) {
		e.sendMessage("");
		e.sendMessage(title);
		e.sendMessage("&e&m----------------------------------------");
		e.sendMessage("");
		for (CommandProperties arg : args) {
			//			e.sendMessage(Text.of("b", "/" + arg.getPrimaryAlias()).append("e", " - ").append("b", arg.getDescription()).toString());
			e.sendMessage("&b/" + arg.getPrimaryAlias() + "&e - &b" + arg.getDescription());
		}

	}

	public final CommandProperties getProperties() {
		return properties;
	}

	public final void enableCommmandList(String title) {

	}

	public final CommandProperties add(String... aliases) {
		return new CommandProperties(aliases);
	}

	public final void setArg(String label, CommandExecutor executor) {
		setArg(label, "", "", executor);
	}

	public final void setArg(String label, String usage, String description, CommandExecutor executor) {

	}

	public final void usageError() {
		throw CommandException.USAGE_ERROR;
	}

}
