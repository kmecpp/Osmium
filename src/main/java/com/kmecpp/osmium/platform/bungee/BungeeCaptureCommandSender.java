package com.kmecpp.osmium.platform.bungee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public class BungeeCaptureCommandSender implements CommandSender {

	private ArrayList<String> messages = new ArrayList<>();

	private static BungeeCaptureCommandSender sender = new BungeeCaptureCommandSender();

	public static String getOutput(String command) {
		BungeeCord.getInstance().getPluginManager().dispatchCommand(sender, command);
		return sender.extractOutput();
	}

	public String extractOutput() {
		String result = String.join("\n", messages);
		messages.clear();
		return result;
	}

	public ArrayList<String> getMessages() {
		return messages;
	}

	@Override
	public void sendMessage(String message) {
		messages.add(message);
	}

	@Override
	public void sendMessage(BaseComponent... message) {
		sendMessage(BaseComponent.toLegacyText(message));
	}

	@Override
	public void sendMessage(BaseComponent message) {
		sendMessage(message.toLegacyText());
	}

	@Override
	public void sendMessages(String... messages) {
		for (String message : messages) {
			this.messages.add(message);
		}
	}

	@Override
	public void addGroups(String... groups) {
	}

	@Override
	public void removeGroups(String... arg0) {
	}

	@Override
	public Collection<String> getGroups() {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return "VF Custom Command Sender";
	}

	@Override
	public Collection<String> getPermissions() {
		return Collections.emptyList();
	}

	@Override
	public void setPermission(String permission, boolean value) {
	}

	@Override
	public boolean hasPermission(String permission) {
		return true;
	}

}
