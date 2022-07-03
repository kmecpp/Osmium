package com.kmecpp.osmium.platform;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.kmecpp.osmium.api.command.CaptureCommandSender;

public class BukkitCaptureCommandSender extends CaptureCommandSender implements ConsoleCommandSender {

	private static final BukkitCaptureCommandSender INSTANCE = new BukkitCaptureCommandSender(NAME);

	public static String[] execute(String command) {
		return INSTANCE.executeGetLines(command);
	}

	private final String name;
	private final PermissibleBase perm;

	public BukkitCaptureCommandSender(String name) {
		this.name = name;
		this.perm = new PermissibleBase(this);
	}

	@Override
	public void dispatchCommand(String command) {
		Bukkit.dispatchCommand(this, command);
	}

	@Override
	public void sendMessage(String message) {
		captureMessage(message);
	}

	@Override
	public void sendMessage(String[] messages) {
		for (String message : messages) {
			captureMessage(message);
		}
	}

	@Override
	public void sendRawMessage(String message) {
		captureMessage(message);
	}

	@Override
	public boolean isPermissionSet(String name) {
		return true;
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return true;
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
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return perm.addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return perm.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return perm.addAttachment(plugin, name, value, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return perm.addAttachment(plugin, ticks);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		perm.removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
		perm.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return perm.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean value) {
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void abandonConversation(Conversation conversation) {
	}

	@Override
	public void abandonConversation(Conversation conversation, ConversationAbandonedEvent event) {
	}

	@Override
	public void acceptConversationInput(String input) {
	}

	@Override
	public boolean beginConversation(Conversation conversation) {
		return false;
	}

	@Override
	public boolean isConversing() {
		return false;
	}

	@Override
	public Spigot spigot() {
		return null;
	}

}
