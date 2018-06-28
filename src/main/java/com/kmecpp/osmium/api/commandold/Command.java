package com.kmecpp.osmium.api.commandold;

import com.kmecpp.osmium.api.CommandSender;

public class Command implements CommandExecutor {

	private String[] aliases;
	private String permission;
	private String description;

	private boolean configured;

	public Command() {
		configure();
	}

	public void configure() {
		configured = true;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {

	}

	public String[] getAliases() {
		return aliases;
	}

	public void setAliases(String[] aliases) {
		if (configured) {
			throw new IllegalStateException("Cannot modify aliases. The command has already been configured.");
		}
		this.aliases = aliases;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
