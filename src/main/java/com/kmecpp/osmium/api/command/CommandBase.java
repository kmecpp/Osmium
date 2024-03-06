package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

import com.kmecpp.osmium.api.MilliTimeUnit;

public class CommandBase {

	private final String[] aliases;

	private ArrayList<String> separateAliases;
	private String description = "";
	private String permission = "";
	private String usage = "";
	private String[] usageParams = new String[0];
	private boolean admin;
	private OverrideMode overrideMode;
	private String[] overrideAliases;
	private CommandExecutor executor;

	private String primaryAlias;
	private String shortestAlias;
	private long cooldown;
	private boolean async;

	public CommandBase(String name, String... aliases) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("First command alias cannot be null or empty!");
		}

		this.aliases = new String[aliases.length + 1];
		this.aliases[0] = name;
		this.primaryAlias = name;
		this.shortestAlias = name;

		for (int i = 0; i < aliases.length; i++) {
			String alias = aliases[i];
			if (alias == null || alias.isEmpty()) {
				continue;
			}

			this.aliases[i + 1] = aliases[i];
			if (alias.length() < this.shortestAlias.length()) {
				this.shortestAlias = alias;
			}
		}
	}

	//	public CommandBase() {
	//		if (name == null) {
	//			throw new NullPointerException("Cannot create command without a name!");
	//			if (!Command.class.isAssignableFrom(this.getClass())) {
	//				throw new NullPointerException("Cannot create command without a name!");
	//			}
	//			CommandProperties command = this.getClass().getAnnotation(CommandProperties.class);
	//			if (command == null) {
	//				throw new IllegalArgumentException("Osmium commands must have an @" + CommandProperties.class.getSimpleName() + " annotation");
	//			}
	//
	//			this.aliases = command.aliases();
	//			this.description = command.description();
	//			this.permission = command.permission();
	//			this.usage = command.usage();
	//			this.usageParams = parseUsage(command.usage());
	//			this.admin = command.admin();
	//			this.console = command.console();
	//		}
	//	}

	public void execute(CommandEvent e) {
		executor.execute(e);
	}

	public String[] getAliases() {
		return aliases;
	}

	//	public void setAliases(String name, String... aliases) {
	//		if (name == null || name.isEmpty()) {
	//			throw new IllegalArgumentException("First command alias cannot be null or empty!");
	//		}
	//
	//		this.aliases = new String[aliases.length + 1];
	//		this.aliases[0] = name;
	//		this.primaryAlias = name;
	//		this.shortestAlias = name;
	//
	//		for (int i = 0; i < aliases.length; i++) {
	//			String alias = aliases[i];
	//			if (alias == null || alias.isEmpty()) {
	//				continue;
	//			}
	//
	//			this.aliases[i + 1] = aliases[i];
	//			if (alias.length() < this.shortestAlias.length()) {
	//				this.shortestAlias = alias;
	//			}
	//		}
	//	}

	public String getPrimaryAlias() {
		return primaryAlias;
	}

	public void setPrimaryAlias(String primaryAlias) {
		this.primaryAlias = primaryAlias;
	}

	public String getShortestAlias() {
		return shortestAlias;
	}

	public CommandBase setDescription(String description) {
		this.description = description;
		return this;
	}

	public CommandBase setPermission(String permission) {
		this.permission = permission;
		return this;
	}

	public CommandBase setAdmin(boolean admin) {
		this.admin = admin;
		return this;
	}

	public OverrideMode getOverrideMode() {
		return overrideMode;
	}

	public void setOverrideMode(OverrideMode overrideMode) {
		this.overrideMode = overrideMode;
	}

	public void setOverrideAliases(String... aliases) {
		this.overrideAliases = aliases;
		this.overrideMode = OverrideMode.SPECIFIC;
	}

	public String[] getOverrideAliases() {
		return overrideAliases;
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

	public void and(String separateAlias) {
		if (separateAliases == null) {
			separateAliases = new ArrayList<>();
		}
		separateAliases.add(separateAlias);
	}

	public boolean hasPermission() {
		return !permission.isEmpty();
	}

	public String getPermission() {
		return permission;
	}

	public CommandBase setCooldown(long cooldown) {
		return setCooldown(cooldown, MilliTimeUnit.MILLISECOND);
	}

	public CommandBase setCooldown(long cooldown, MilliTimeUnit timeUnit) {
		this.cooldown = cooldown * timeUnit.getMillisecondTime();
		return this;
	}

	public long getCooldown() {
		return cooldown;
	}

	public CommandBase setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public boolean isAsync() {
		return async;
	}

	public boolean hasUsage() {
		return !usage.isEmpty();
	}

	public String getUsage() {
		return usage;
	}

	public CommandBase setUsage(String usage) {
		this.usage = usage;
		this.usageParams = parseUsage(usage);
		return this;
	}

	public String getUsageHighlight(int index) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String param : usageParams) {
			if (i == index) {
				sb.append(Chat.DARK_RED + "<" + param + ">" + Chat.RED);
			} else {
				sb.append("<" + param + ">");
			}
			sb.append(' ');
			i++;
		}
		return sb.toString();
	}

	public String getUsageParameter(int index) {
		return index >= 0 && index < usageParams.length ? usageParams[index] : null;
	}

	public String[] getUsageParams() {
		return usageParams;
	}

	public boolean isAdmin() {
		return admin;
	}

	//<add/remove>
	private static String[] parseUsage(String usage) {
		//TODO: This isn't working for some existing usages. Forgot which ones... but should revisit this
		//		for (String part : usage.split("\\s+")) {
		//		}
		//		System.out.println(usage);
		String[] params = usage.split(">\\s+<");
		//		System.out.println(Arrays.asList(params));
		for (int i = 0; i < params.length; i++) {
			params[i] = params[i].replace("<", "").replace(">", "").trim();
			if (params[i].contains(" ")) {
				params[i] = params[i].split(" ")[0];
			}
		}
		return params;
	}

}
