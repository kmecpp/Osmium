package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.command.ConsoleCommandSender;

public class SpongeConsoleCommandSender implements ConsoleCommandSender {

	private ConsoleSource sender;

	public SpongeConsoleCommandSender(ConsoleSource sender) {
		this.sender = sender;
	}

	@Override
	public ConsoleSource getSource() {
		return sender;
	}

	@Override
	public boolean isOp() {
		return sender.hasPermission("*");
	}

	@Override
	public void setOp(boolean value) {
		throw new UnsupportedOperationException("Cannot change operator status of server console");
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(Text.of(message));
	}

}
