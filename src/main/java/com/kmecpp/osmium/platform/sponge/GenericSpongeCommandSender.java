package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.command.CommandSender;

public class GenericSpongeCommandSender implements CommandSender {

	private CommandSource sender;

	public GenericSpongeCommandSender(CommandSource sender) {
		this.sender = sender;
	}

	@Override
	public CommandSource getSource() {
		return sender;
	}

	@Override
	public boolean isOp() {
		return sender.hasPermission("*");
	}

	@Override
	public void setOp(boolean value) {
		throw new UnsupportedOperationException("Cannot change operator status of generic command sender");
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
	public void sendRawMessage(String message) {
		sender.sendMessage(Text.of(message));
	}

}
