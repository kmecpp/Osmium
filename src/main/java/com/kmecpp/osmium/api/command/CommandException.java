package com.kmecpp.osmium.api.command;

@SuppressWarnings("serial")
public class CommandException extends RuntimeException {

	public static final CommandException USAGE_ERROR = new CommandException("Usage error!");
	public static final CommandException PLAYER_NOT_FOUND = new CommandException("That player is not online!");
	public static final CommandException PLAYERS_ONLY = new CommandException("This command cannot be executed from the console!");
	public static final CommandException LACKS_PERMISSION = new CommandException("You do not have permission to perform this command!");

	//	private CommandResult result;
	private final String message;

	//	public CommandException(CommandResult result) {
	//		this(result, "");
	//	}

	public CommandException(String message) {
		//		this.result = result;
		this.message = message;
	}

	//	public CommandResult getResult() {
	//		return result;
	//	}

	@Override
	public String getMessage() {
		return message;
	}

}
