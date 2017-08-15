package com.kmecpp.osmium.api.command;

public class CommandException extends RuntimeException {

	private static final long serialVersionUID = 4700707760543287464L;

	public static final CommandException USAGE_ERROR = new CommandException();
	public static final CommandException PLAYERS_ONLY = new CommandException();
	public static final CommandException UNKNOWN_COMMAND = new CommandException();
	public static final CommandException LACKS_PERMISSION = new CommandException();

	private final String message;

	public CommandException() {
		this(null);
	}

	public CommandException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocalizedMessage() {
		return message;
	}

}
