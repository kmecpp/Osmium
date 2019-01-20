package com.kmecpp.osmium.platform.bukkit;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.kmecpp.osmium.api.command.CommandSender;

public class BukkitConsoleCommandRedirect implements ConsoleCommandSender {

	private CommandSender output;
	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	public BukkitConsoleCommandRedirect(CommandSender output) {
		this.output = output;
	}

	@Override
	public void sendMessage(String message) {
		output.send(message);
	}

	@Override
	public void sendRawMessage(String message) {
		output.sendMessage(message);
	}

	@Override
	public boolean hasPermission(String name) {
		return true;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return true;
	}

	@Override
	public void sendMessage(String[] messages) {
		console.sendMessage(messages);
	}

	@Override
	public Server getServer() {
		return console.getServer();
	}

	@Override
	public String getName() {
		return console.getName();
	}

	@Override
	public Spigot spigot() {
		return console.spigot();
	}

	@Override
	public boolean isPermissionSet(String name) {
		return console.isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return console.isPermissionSet(perm);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return console.addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return console.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return console.addAttachment(plugin, name, value, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return console.addAttachment(plugin, ticks);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		console.removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
		console.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return console.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return console.isOp();
	}

	@Override
	public void setOp(boolean value) {
		console.setOp(value);
	}

	@Override
	public boolean isConversing() {
		return console.isConversing();
	}

	@Override
	public void acceptConversationInput(String input) {
		console.acceptConversationInput(input);
	}

	@Override
	public boolean beginConversation(Conversation conversation) {
		return console.beginConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation) {
		console.abandonConversation(conversation);
	}

	@Override
	public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
		console.abandonConversation(conversation, details);
	}

}
