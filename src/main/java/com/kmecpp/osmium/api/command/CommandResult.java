package com.kmecpp.osmium.api.command;

public enum CommandResult {

	SUCCESS(true),
	ERROR(false),
	USAGE_ERROR(false),
	LACKS_PERMISSION(false),
	UNKNOWN_COMMAND(false),
	PLAYER_NOT_FOUND(false),
	PLAYER_ONLY(false);

	private final boolean value;
	private String message = null;

	private CommandResult(boolean value) {
		this.value = value;
	}

	public boolean boolValue() {
		return this.value;
	}

	/**
	 * Sets the global result message for this enum
	 * 
	 * @param message
	 *            the new message
	 * @return the command result for chaining
	 */
	public CommandResult message(String message) {
		this.message = message;
		return this;
	}

	//TODO This is stupid, enums are global and static
	/**
	 * Extracts the message, returning the String and then setting it to null
	 * 
	 * @return the message stored in this command result
	 */
	public String extractMessage() {
		String msg = this.message;
		this.message = null;
		return msg;
	}

	//	public static class CR {
	//
	//		public static final CR SUCCESS = new CR(CommandResult.SUCCESS);
	//		public static final CR ERROR = new CR(CommandResult.ERROR);
	//		public static final CR USAGE_ERROR = new CR(CommandResult.USAGE_ERROR);
	//		public static final CR LACKS_PERMISSION = new CR(CommandResult.LACKS_PERMISSION);
	//		public static final CR UNKNOWN_COMMAND = new CR(CommandResult.UNKNOWN_COMMAND);
	//		public static final CR PLAYER_NOT_FOUND = new CR(CommandResult.PLAYER_NOT_FOUND);
	//		public static final CR PLAYER_ONLY = new CR(CommandResult.PLAYER_ONLY);
	//
	//		private CommandResult value;
	//		private String message;
	//
	//		private CR(CommandResult value) {
	//			this.value = value;
	//		}
	//
	//		public CR message(String message) {
	//			CR cr = new CR(value);
	//			cr.message = message;
	//			return cr;
	//		}
	//
	//		public String getMessage() {
	//			return message;
	//		}
	//
	//		public CommandResult getValue() {
	//			return value;
	//		}
	//
	//	}

}