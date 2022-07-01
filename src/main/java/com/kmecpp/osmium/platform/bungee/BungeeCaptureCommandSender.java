package com.kmecpp.osmium.platform.bungee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.kmecpp.osmium.api.CaptureCommandSender;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public class BungeeCaptureCommandSender implements CommandSender, CaptureCommandSender {

	private static final BungeeCaptureCommandSender INSTANCE = new BungeeCaptureCommandSender("Osmium Capture Command Sender");

	public static String[] execute(String command) {
		BungeeCord.getInstance().getPluginManager().dispatchCommand(INSTANCE, command);
		String[] result = INSTANCE.getOutput();
		INSTANCE.output.clear();
		return result;
	}

	private final String name;
	private final ArrayList<String> output;

	public BungeeCaptureCommandSender(String name) {
		this.name = name;
		this.output = new ArrayList<>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getOutput() {
		return output.toArray(new String[0]);
	}

	@Override
	public void sendMessage(String message) {
		output.add(message);
	}

	@Override
	public void sendMessages(String... messages) {
		for (String message : messages) {
			output.add(message);
		}
	}

	@Override
	public void sendMessage(BaseComponent... components) {
		for (BaseComponent component : components) {
			output.add(component.toPlainText());
		}
	}

	@Override
	public void sendMessage(BaseComponent component) {
		output.add(component.toPlainText());
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
