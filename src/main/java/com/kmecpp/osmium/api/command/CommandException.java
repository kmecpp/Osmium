package com.kmecpp.osmium.api.command;

@SuppressWarnings("serial")
public class CommandException extends RuntimeException {

	public static final CommandException USAGE_ERROR = new CommandException("Usage error!");
	public static final CommandException PLAYER_NOT_FOUND = new CommandException("That player is not online!");

	//	private CommandResult result;
	private String message;

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
