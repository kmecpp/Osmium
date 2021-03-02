package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.command.CommandSender;

public class CommandRedirectSender implements CommandSender {

	private CommandSender sender;
	private CommandSender receiver;

	public CommandRedirectSender(CommandSender sender, CommandSender receiver) {
		if (sender instanceof CommandRedirectSender) {
			throw new IllegalArgumentException();
		}
		this.sender = sender;
		this.receiver = receiver;
	}

	@Override
	public Object getSource() {
		return sender.getSource();
	}

	@Override
	public void sendMessage(String message) {
		receiver.sendMessage(message);
	}

	@Override
	public boolean isOp() {
		return sender.isOp();
	}

	@Override
	public void setOp(boolean value) {
		sender.setOp(value);
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

}
