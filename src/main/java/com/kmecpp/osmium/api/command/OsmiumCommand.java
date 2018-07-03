package com.kmecpp.osmium.api.command;

public abstract class OsmiumCommand {

	private Command properties;

	public OsmiumCommand() {
		this.properties = this.getClass().getAnnotation(Command.class);
	}

	public Command getProperties() {
		return properties;
	}

	public void configure() {

		//<$p_player>
	}

	public void execute(CommandEvent e) {

	}

	public void enableCommmandList(String title) {

	}

	public CommandProperties add(String... aliases) {
		return new CommandProperties(aliases);
	}

	public void setArg(String label, CommandExecutor executor) {
		setArg(label, "", "", executor);
	}

	public void setArg(String label, String usage, String description, CommandExecutor executor) {

	}

	public void usageError() {
		throw new CommandException(CommandResult.USAGE_ERROR);
	}

}
