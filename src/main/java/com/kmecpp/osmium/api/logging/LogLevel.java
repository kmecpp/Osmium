package com.kmecpp.osmium.api.logging;

import com.kmecpp.osmium.api.command.Chat;

public enum LogLevel {

	DEBUG,
	INFO,
	WARN,
	ERROR,

	;

	private Chat color;

	static {
		DEBUG.color = Chat.WHITE;
		INFO.color = Chat.GREEN;
		WARN.color = Chat.YELLOW;
		ERROR.color = Chat.RED;
		//		if (Platform.isBukkit()) {
		//			DEBUG.colorImpl = ChatColor.WHITE;
		//			INFO.colorImpl = ChatColor.GREEN;
		//			WARN.colorImpl = ChatColor.YELLOW;
		//			ERROR.colorImpl = ChatColor.RED;
		//		} else if (Platform.isSponge()) {
		//			DEBUG.colorImpl = TextColors.WHITE;
		//			INFO.colorImpl = TextColors.GREEN;
		//			WARN.colorImpl = TextColors.YELLOW;
		//			ERROR.colorImpl = TextColors.RED;
		//		}
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

	public Chat getColor() {
		return color;
	}

	public java.util.logging.Level getLevel() {
		switch (this) {
		case DEBUG:
		case INFO:
			return java.util.logging.Level.INFO;
		case WARN:
			return java.util.logging.Level.WARNING;
		case ERROR:
			return java.util.logging.Level.SEVERE;
		default:
			return java.util.logging.Level.SEVERE;
		}
	}

}
