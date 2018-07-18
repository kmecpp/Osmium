package com.kmecpp.osmium.api.logging;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.core.CoreOsmiumConfiguration;

public class OsmiumLogger {

	private static Logger logger = LoggerFactory.getLogger(AppInfo.NAME);

	public static final void debug(String message) {
		if (Platform.isBukkit()) {
			logBukkit(LogLevel.DEBUG, AppInfo.NAME, message);
		} else {
			logger.debug(message);
		}
	}

	public static final void info(String message) {
		if (Platform.isBukkit()) {
			logBukkit(LogLevel.INFO, AppInfo.NAME, message);
		} else {
			logger.info(message);
		}
	}

	public static final void warn(String message) {
		if (Platform.isBukkit()) {
			logBukkit(LogLevel.WARN, AppInfo.NAME, message);
		} else {
			logger.warn(message);
		}
	}

	public static final void error(String message) {
		if (Platform.isBukkit()) {
			logBukkit(LogLevel.ERROR, AppInfo.NAME, message);
		} else {
			logger.error(message);
		}
	}

	/**
	 * Logs a message to the console with the given prefix
	 *
	 * @param level
	 *            the level of the message
	 * @param prefix
	 *            the prefix of the message
	 * @param message
	 *            the message
	 */
	public static void logBukkit(LogLevel level, String prefix, String message) {
		if (level == LogLevel.DEBUG && !CoreOsmiumConfiguration.debug) {
			return;
		}
		if (Bukkit.getConsoleSender() != null) { //This seems to only be an issue on legacy servers during startup
			Bukkit.getConsoleSender().sendMessage(""
					+ ChatColor.DARK_AQUA + "["
					+ ChatColor.AQUA + prefix + (level != LogLevel.DEBUG && level != LogLevel.INFO ? ChatColor.DARK_AQUA + "|" + level.getColor() + level : "")
					+ ChatColor.DARK_AQUA + "] "
					+ level.getColor() + message);
		} else {
			Bukkit.getLogger().log(level.getLevel(), "[" + prefix + "] " + message);
		}
	}

}
