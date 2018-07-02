package com.kmecpp.osmium.api.command;

public abstract class OsmiumCommand {
	
	public void configure() {
		
	}

	public void execute(CommandEvent e) {

	}
	
	public void registerArg(String label, String usage) {
		
	}

	public void usageError() {
		throw new CommandException(CommandResult.USAGE_ERROR);
	}

}
