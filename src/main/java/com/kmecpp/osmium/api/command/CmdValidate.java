package com.kmecpp.osmium.api.command;

public abstract class CmdValidate {

	private CmdValidate() {
	}

	//	/**
	//	 * Validates that the given {@link CommandSender} is an operator by throwing
	//	 * a {@link CommandException} if they are not.
	//	 * 
	//	 * @param sender
	//	 *            the sender to validate
	//	 * @throws CommandException
	//	 *             if the sender is not a server operator
	//	 */
	//	public static void isOp(CommandSender sender) throws CommandException {
	//		if (sender.isOp()) {
	//			throw new CommandException(CommandResult.LACKS_PERMISSION);
	//		}
	//	}
	//
	//	/**
	//	 * Validates that the given {@link CommandSender} is an in-game player by
	//	 * throwing a {@link CommandException} if they are not.
	//	 * 
	//	 * @param sender
	//	 *            the sender to validate
	//	 * @throws CommandException
	//	 *             if the sender is not a player
	//	 */
	//	public static void isPlayer(CommandSender sender) throws CommandException {
	//		if (!(sender instanceof Player)) {
	//			throw new CommandException(CommandResult.PLAYER_ONLY);
	//		}
	//	}

	/**
	 * Validates that the size of a given array of arguments is less than the
	 * maximum size by throwing a {@link CommandException} if its length is
	 * greater than the max
	 * 
	 * @param args
	 *            the argument list to validate
	 * @param max
	 *            the maximum number of arguments permitted
	 * @throws CommandException
	 *             if the args length is greater than the max
	 */
	public static void argsLength(String[] args, int max) throws CommandException {
		argsLength(args, 0, max);
	}

	/**
	 * Validates that the size given array of arguments is within a given range
	 * by throwing a {@link CommandException} if it's length is strictly less
	 * than the min or greater than the max
	 * 
	 * @param args
	 *            the argument list to validate
	 * @param min
	 *            the minimum number of arguments permitted
	 * @param max
	 *            the maximum number of arguments permitted
	 * @throws CommandException
	 *             if the args length is outside the given range
	 */
	public static void argsLength(String[] args, int min, int max) throws CommandException {
		if (args.length < min || args.length > max) {
			throw CommandException.USAGE_ERROR;
		}
	}

}
