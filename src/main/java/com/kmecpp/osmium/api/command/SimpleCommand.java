package com.kmecpp.osmium.api.command;

public class SimpleCommand {

	private final String[] aliases;

	private String description = "";
	private String permission = "";
	private String usage = "";
	private String[] usageParams;
	private boolean admin;
	private boolean console;
	private CommandExecutor executor;

	private String primaryAlias;
	private String shortestAlias;

	//	public CommandProperties(String... aliases) {
	//		this.aliases = aliases;
	//	}

	public SimpleCommand(String name, String... aliases) {
		if (name == null) {
			if (!Command.class.isAssignableFrom(this.getClass())) {
				throw new NullPointerException("Cannot create command without a name!");
			}
			CommandProperties command = this.getClass().getAnnotation(CommandProperties.class);
			if (command == null) {
				throw new IllegalArgumentException("Osmium commands must have an @" + CommandProperties.class.getSimpleName() + " annotation");
			}

			this.aliases = command.aliases();
			this.description = command.description();
			this.permission = command.permission();
			this.usage = command.usage();
			this.usageParams = parseUsage(command.usage());
			this.admin = command.admin();
			this.console = command.console();
		} else {
			this.aliases = new String[aliases.length + 1];
			this.aliases[0] = name;
			for (int i = 0; i < aliases.length; i++) {
				String alias = aliases[i];
				this.aliases[i + 1] = alias;
			}
		}

		if (this.aliases == null || this.aliases.length == 0) {
			throw new IllegalArgumentException("Command must have at least one alias!");
		}

		this.primaryAlias = this.aliases[0];
		for (String alias : this.aliases) {
			if (this.shortestAlias == null || alias.length() < this.shortestAlias.length()) {
				this.shortestAlias = alias;
			}
		}
	}

	public void execute(CommandEvent e) {
		executor.execute(e);
	}

	public String[] getAliases() {
		return aliases;
	}

	public String getPrimaryAlias() {
		return primaryAlias;
	}

	public void setPrimaryAlias(String primaryAlias) {
		this.primaryAlias = primaryAlias;
	}

	public String getShortestAlias() {
		return shortestAlias;
	}

	public SimpleCommand setDescription(String description) {
		this.description = description;
		return this;
	}

	public SimpleCommand setPermission(String permission) {
		this.permission = permission;
		return this;
	}

	public SimpleCommand setUsage(String usage) {
		this.usage = usage;
		return this;
	}

	public SimpleCommand setAdmin(boolean admin) {
		this.admin = admin;
		return this;
	}

	public SimpleCommand setConsole(boolean console) {
		this.console = console;
		return this;
	}

	public void setExecutor(CommandExecutor executor) {
		this.executor = executor;
	}

	public CommandExecutor getExecutor() {
		return executor;
	}

	public boolean hasDescription() {
		return !description.isEmpty();
	}

	public String getDescription() {
		return description;
	}

	protected void checkPermission(CommandEvent event) {
		if (!isAllowed(event.getSender())) {
			throw CommandException.LACKS_PERMISSION;
		}
	}

	protected boolean isAllowed(CommandSender sender) {
		return this.admin ? sender.isOp()
				: this.hasPermission() ? sender.hasPermission(this.permission)
						: true;
	}

	public boolean hasPermission() {
		return !permission.isEmpty();
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasUsage() {
		return !usage.isEmpty();
	}

	public String getUsage() {
		return usage;
	}

	public String getUsageHighlight(int index) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String param : usageParams) {
			if (i == index) {
				sb.append(Chat.DARK_RED + Chat.BOLD.toString() + "<" + param + ">" + Chat.RED);
			} else {
				sb.append("<" + param + ">");
			}
			i++;
		}
		return sb.toString();
	}

	public String[] getUsageParams() {
		return usageParams;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isConsole() {
		return console;
	}

	//<add/remove>
	private static String[] parseUsage(String usage) {
		String[] params = usage.split(">.+<");
		for (int i = 0; i < params.length; i++) {
			params[i] = params[i].replace("<", "").replace(">", "").trim();
		}
		return params;
	}

}
