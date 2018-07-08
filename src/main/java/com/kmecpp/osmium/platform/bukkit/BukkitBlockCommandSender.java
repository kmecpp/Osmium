package com.kmecpp.osmium.platform.bukkit;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.command.BlockCommandSender;

public class BukkitBlockCommandSender implements BlockCommandSender {

	private org.bukkit.command.BlockCommandSender sender;

	public BukkitBlockCommandSender(org.bukkit.command.BlockCommandSender sender) {
		this.sender = sender;
	}

	@Override
	public org.bukkit.command.BlockCommandSender getSource() {
		return sender;
	}

	@Override
	public boolean isOp() {
		return sender.isOp();
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
	public void sendRawMessage(String message) {
		sender.sendMessage(message);
	}

	@Override
	public Block getBlock() {
		return new BukkitBlock(sender.getBlock());
	}

}
