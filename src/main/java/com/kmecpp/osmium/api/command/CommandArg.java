package com.kmecpp.osmium.api.command;

import com.kmecpp.jlib.utils.StringUtil;

import net.md_5.bungee.api.ChatColor;

public class CommandArg {

	private final String label;
	private final String description;
	private final int flags;

	private final Params params;

	public CommandArg(String label, String parameters, int flags, String description) {
		this.label = label;
		this.flags = flags;
		this.description = description;

		this.params = new Params(parameters);
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public Params getParams() {
		return params;
	}

	public boolean isAdminOnly() {
		return (flags & ComplexCommand.ADMIN) == ComplexCommand.ADMIN;
	}

	public boolean isPlayersOnly() {
		return (flags & ComplexCommand.PLAYER) == ComplexCommand.PLAYER;
	}

	public boolean isValidLength(String[] args) {
		return args.length >= params.required && (params.total == -1 || args.length <= params.total);
	}

	public String checkArgs(String[] args) {
		StringBuilder sb = new StringBuilder();
		boolean valid = true;
		for (int i = 0; i < params.parts.length; i++) { //Sub commands are required (<>)
			sb.append(" ");

			String param = params.getParam(i);
			if (!params.isSubCommand(i)) {
				sb.append(param);
			} else if (valid) {
				argCheck: {
					for (String sub : param.substring(1, param.length() - 1).split("/")) {
						if (sub.equalsIgnoreCase(args[i])) {
							break argCheck;
						}
					}
					valid = false;
				}
				sb.append(ChatColor.DARK_RED + param + ChatColor.RED);
			} else {
				sb.append(param);
			}
		}
		if (!valid) {
			return label + sb.toString();
		}
		return "";
	}

	public static class Params {

		private final String params;
		private final String[] parts;

		private final int required;
		private final int optional;
		private final int total;

		public Params(String params) {
			params = params.endsWith("...") ? params.substring(0, params.length() - 3) : params;
			this.params = params;
			this.parts = params.split(" ");
			this.required = StringUtil.count(params, "<") + StringUtil.count(params, "[");
			this.optional = StringUtil.count(params, "{");
			this.total = params.endsWith("...") ? -1 : required + optional;
		}

		public String getParams() {
			return params;
		}

		public int getRequired() {
			return required;
		}

		public int getOptional() {
			return optional;
		}

		public int getTotal() {
			return total;
		}

		public String getParam(int index) {
			return parts[index];
		}

		public boolean isSubCommand(int index) {
			return index < parts.length && parts[index].startsWith("[");
		}

		@Override
		public String toString() {
			return params;
		}

	}
}
