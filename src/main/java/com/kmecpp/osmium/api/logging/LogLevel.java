package com.kmecpp.osmium.api.logging;

import org.bukkit.ChatColor;
import org.spongepowered.api.text.format.TextColors;

import com.kmecpp.osmium.api.platform.Platform;

public enum LogLevel {

	DEBUG,
	INFO,
	WARN,
	ERROR,

	;

	private Object colorImpl;

	static {
		if (Platform.isBukkit()) {
			DEBUG.colorImpl = ChatColor.WHITE;
			INFO.colorImpl = ChatColor.GREEN;
			WARN.colorImpl = ChatColor.YELLOW;
			ERROR.colorImpl = ChatColor.RED;
		} else if (Platform.isSponge()) {
			DEBUG.colorImpl = TextColors.WHITE;
			INFO.colorImpl = TextColors.GREEN;
			WARN.colorImpl = TextColors.YELLOW;
			ERROR.colorImpl = TextColors.RED;
		}
	}

	public boolean isDebug() {
		return this == DEBUG;
	}

	public boolean isInfo() {
		return this == INFO;
	}

	public boolean isWarn() {
		return this == WARN;
	}

	public boolean isError() {
		return this == ERROR;
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
			return java.util.logging.Level.SEVERE;
		default:
			return java.util.logging.Level.ALL;
		}
	}

}
