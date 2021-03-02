package com.kmecpp.osmium.platform.bungee;

import java.util.Collection;
import java.util.Collections;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public class BungeeCommandRedirectSender implements CommandSender {

	private CommandSender receiver;
	private CommandSender sender = BungeeCord.getInstance().getConsole();

	public BungeeCommandRedirectSender(CommandSender output) {
		this.receiver = output;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendMessages(String... messages) {
		receiver.sendMessages(messages);
	}

	@Override
	public void sendMessage(BaseComponent... messages) {
		receiver.sendMessage(messages);
	}

	@Override
	public void sendMessage(BaseComponent message) {
		receiver.sendMessage(message);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendMessage(String message) {
		receiver.sendMessage(message);
	}

	@Override
	public boolean hasPermission(String name) {
		return true;
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public Collection<String> getGroups() {
		return Collections.emptySet();
	}

	@Override
	public void addGroups(String... var1) {
	}

	@Override
	public void removeGroups(String... var1) {
	}

	@Override
	public void setPermission(String var1, boolean var2) {
	}

	@Override
	public Collection<String> getPermissions() {
		return Collections.emptySet();
	}

}
