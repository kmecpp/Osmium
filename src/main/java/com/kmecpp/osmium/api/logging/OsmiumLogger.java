package com.kmecpp.osmium.api.logging;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.core.CoreOsmiumConfiguration;

public class OsmiumLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInfo.NAME);
	private static final String PREFIX = Chat.DARK_AQUA + "[" + Chat.AQUA + AppInfo.NAME + "%L" + Chat.DARK_AQUA + "] ";

	public static final void debug(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.DEBUG, AppInfo.NAME, message);
		} else {
			LOGGER.debug(message);
		}
	}

	public static final void info(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.INFO, AppInfo.NAME, message);
		} else {
			LOGGER.info(message);
		}
	}

	public static final void warn(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.WARN, AppInfo.NAME, message);
		} else {
			LOGGER.warn(message);
		}
	}

	public static final void error(String message) {
		if (CoreOsmiumConfiguration.coloredConsole) {
			log(LogLevel.ERROR, AppInfo.NAME, message);
		} else {
			LOGGER.error(message);
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
	private static void log(LogLevel level, String prefix, String message) {
		if (level == LogLevel.DEBUG && !CoreOsmiumConfiguration.debug) {
			return;
		}

		boolean displayLevel = level != LogLevel.DEBUG && level != LogLevel.INFO;
		if (Platform.isBukkit()) {
			if (Bukkit.getConsoleSender() != null) { //This seems to only be an issue on legacy servers during startup
				String start = PREFIX.replace("%L", (displayLevel ? ChatColor.DARK_AQUA + "|" + level.getColorImplementation() + level : ""));
				Bukkit.getConsoleSender().sendMessage(start + level.getColorImplementation() + message);
			} else {
				Bukkit.getLogger().log(level.getLevel(), "[" + prefix + "] " + message);
			}
		} else if (Platform.isSponge()) {
			Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_AQUA, "[", TextColors.AQUA, prefix,
					(displayLevel ? Text.of(TextColors.DARK_AQUA, "|", level.getColorImplementation(), level) : Text.EMPTY),
					TextColors.DARK_AQUA, "] ", level.getColorImplementation(), message));
		} else {
			System.out.println(message);
			//			if (LOGGER instanceof NOPLogger) {
			//				System.out.println(message);
			//			} else {
			//				LOGGER.info(message);
			//			}
		}
	}

}
