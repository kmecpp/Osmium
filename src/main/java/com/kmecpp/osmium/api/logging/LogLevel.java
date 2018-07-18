package com.kmecpp.osmium.api.logging;

import org.bukkit.ChatColor;

public enum LogLevel {

	DEBUG(ChatColor.WHITE),
	INFO(ChatColor.GREEN),
	WARN(ChatColor.YELLOW),
	ERROR(ChatColor.RED),
	FATAL(ChatColor.RED)

	;

	private final String color;

	private LogLevel(ChatColor color) {
		this.color = color.toString();
	}

	public String getColor() {
		return color;
	}

	public java.util.logging.Level getLevel() {
		switch (this) {
		case INFO:
			return java.util.logging.Level.INFO;
		case WARN:
			return java.util.logging.Level.WARNING;
		case ERROR:
		case FATAL:
			return java.util.logging.Level.SEVERE;
		default:
			return java.util.logging.Level.ALL;
		}
	}

}
