package com.kmecpp.osmium.platform.bungee;

import java.util.Collection;
import java.util.Collections;

import com.kmecpp.osmium.api.command.CaptureCommandSender;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public class BungeeCaptureCommandSender extends CaptureCommandSender implements CommandSender {

	private static final BungeeCaptureCommandSender INSTANCE = new BungeeCaptureCommandSender(NAME);

	public static String[] execute(String command) {
		return INSTANCE.executeGetLines(command);
	}

	private final String name;

	public BungeeCaptureCommandSender(String name) {
		this.name = name;
	}

	@Override
	public void dispatchCommand(String command) {
		BungeeCord.getInstance().getPluginManager().dispatchCommand(this, command);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void sendMessage(String message) {
		captureMessage(message);
	}

	@Override
	public void sendMessages(String... messages) {
		for (String message : messages) {
			captureMessage(message);
		}
	}

	@Override
	public void sendMessage(BaseComponent... components) {
		for (BaseComponent component : components) {
			captureMessage(component.toPlainText());
		}
	}

	@Override
	public void sendMessage(BaseComponent component) {
		captureMessage(component.toPlainText());
	}

	@Override
	public Collection<String> getGroups() {
		return Collections.emptyList();
	}

	@Override
	public void addGroups(String... var1) {
	}

	@Override
	public void removeGroups(String... var1) {
	}

	@Override
	public boolean hasPermission(String var1) {
		return true;
	}

	@Override
	public void setPermission(String var1, boolean var2) {
	}

	@Override
	public Collection<String> getPermissions() {
		return Collections.emptyList();
	}

}
