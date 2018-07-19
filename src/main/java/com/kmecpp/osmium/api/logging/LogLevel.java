package com.kmecpp.osmium.api.logging;

import org.bukkit.ChatColor;
import org.spongepowered.api.text.format.TextColors;

import com.kmecpp.osmium.api.platform.Platform;

public enum LogLevel {

	DEBUG,
	INFO,
	WARN,
	ERROR,
	FATAL,

	;

	private Object colorImpl;

	static {
		if (Platform.isBukkit()) {
			DEBUG.colorImpl = ChatColor.WHITE;
			INFO.colorImpl = ChatColor.GREEN;
			WARN.colorImpl = ChatColor.YELLOW;
			ERROR.colorImpl = ChatColor.RED;
			FATAL.colorImpl = ChatColor.RED;
		} else if (Platform.isSponge()) {
			DEBUG.colorImpl = TextColors.WHITE;
			INFO.colorImpl = TextColors.GREEN;
			WARN.colorImpl = TextColors.YELLOW;
			ERROR.colorImpl = TextColors.RED;
			FATAL.colorImpl = TextColors.RED;
		}
	}

	public Object getColorImplementation() {
		return colorImpl;
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
