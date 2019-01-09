package com.kmecpp.osmium.platform.sponge;

import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.text.Text;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.command.BlockCommandSender;

public class SpongeBlockCommandSender implements BlockCommandSender {

	private CommandBlockSource sender;

	public SpongeBlockCommandSender(CommandBlockSource sender) {
		this.sender = sender;
	}

	@Override
	public CommandBlockSource getSource() {
		return sender;
	}

	@Override
	public boolean isOp() {
		return sender.hasPermission("*");
	}

	@Override
	public void setOp(boolean value) {
		throw new UnsupportedOperationException("Cannot change operator status of a block");
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

	@Override
	public Block getBlock() {
		return new SpongeBlock(sender.getLocation());
	}

}
