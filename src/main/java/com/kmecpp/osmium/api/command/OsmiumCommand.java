package com.kmecpp.osmium.api.command;

public abstract class OsmiumCommand {

	public abstract void execute(CommandEvent e);

	public void usageError() {
		throw new CommandException(CommandResult.USAGE_ERROR);
	}

}
