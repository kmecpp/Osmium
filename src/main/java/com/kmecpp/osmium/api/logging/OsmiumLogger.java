package com.kmecpp.osmium.api.logging;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.core.CoreOsmiumConfiguration;

public class OsmiumLogger {

	private static Logger logger = LoggerFactory.getLogger(AppInfo.NAME);

	static {
		Osmium.reloadConfig(CoreOsmiumConfiguration.class);
	}

	public static final void debug(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.DEBUG, AppInfo.NAME, message);
		} else {
			logger.debug(message);
		}
	}

	public static final void info(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.INFO, AppInfo.NAME, message);
		} else {
			logger.info(message);
		}
	}

	public static final void warn(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.WARN, AppInfo.NAME, message);
		} else {
			logger.warn(message);
		}
	}

	public static final void error(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.ERROR, AppInfo.NAME, message);
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
	public static void log(LogLevel level, String prefix, String message) {
		if (level == LogLevel.DEBUG && !CoreOsmiumConfiguration.debug) {
			return;
		}

		boolean displayLevel = level != LogLevel.DEBUG && level != LogLevel.INFO;
		if (Platform.isBukkit()) {
			if (Bukkit.getConsoleSender() != null) { //This seems to only be an issue on legacy servers during startup
				Bukkit.getConsoleSender().sendMessage(""
						+ ChatColor.DARK_AQUA + "["
						+ ChatColor.AQUA + prefix + (displayLevel ? ChatColor.DARK_AQUA + "|" + level.getColorImplementation() + level : "")
						+ ChatColor.DARK_AQUA + "] "
						+ level.getColorImplementation() + message);
			} else {
				Bukkit.getLogger().log(level.getLevel(), "[" + prefix + "] " + message);
			}
		} else if (Platform.isSponge()) {
			Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_AQUA, "[", TextColors.AQUA, prefix,
					(displayLevel ? Text.of(TextColors.DARK_AQUA, "|", level.getColorImplementation(), level) : Text.EMPTY),
					TextColors.DARK_AQUA, "] ", level.getColorImplementation(), message));
		} else {
			logger.info("[" + prefix + "] " + message);
		}
	}

}
