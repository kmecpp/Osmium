package com.kmecpp.osmium.api.command;

@SuppressWarnings("serial")
public class CommandException extends RuntimeException {

	private CommandResult result;
	private String message;

	public CommandException(CommandResult result) {
		this(result, "");
	}

	public CommandException(CommandResult result, String message) {
		this.result = result;
		this.message = message;
	}

	public CommandResult getResult() {
		return result;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
