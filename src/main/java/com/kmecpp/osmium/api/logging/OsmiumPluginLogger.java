package com.kmecpp.osmium.api.logging;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.core.CoreOsmiumConfig;

public class OsmiumPluginLogger {

	private final String NAME;
	private final Logger LOGGER;
	private final String PREFIX;

	public OsmiumPluginLogger(String name) {
		this.NAME = name;
		this.LOGGER = LoggerFactory.getLogger(NAME);
		this.PREFIX = Chat.DARK_AQUA + "[" + Chat.AQUA + NAME + "%L" + Chat.DARK_AQUA + "] ";
	}

	public final void debug(String message) {
		if (CoreOsmiumConfig.coloredConsole) {
			log(LogLevel.DEBUG, NAME, message);
		} else {
			LOGGER.debug(message);
		}
	}

	public final void info(String message) {
		if (CoreOsmiumConfig.coloredConsole) {
			log(LogLevel.INFO, NAME, message);
		} else {
			LOGGER.info(message);
		}
	}

	public final void warn(String message) {
		if (CoreOsmiumConfig.coloredConsole) {
			log(LogLevel.WARN, NAME, message);
		} else {
			LOGGER.warn(message);
		}
	}

	public final void error(String message) {
		if (CoreOsmiumConfig.coloredConsole) {
			log(LogLevel.ERROR, NAME, message);
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
	private void log(LogLevel level, String prefix, String message) {
		if (level == LogLevel.DEBUG && !CoreOsmiumConfig.debug) {
			return;
		}

		boolean displayLevel = level != LogLevel.DEBUG && level != LogLevel.INFO;
		if (Platform.isBukkit()) {
			if (Bukkit.getServer() == null) {
				printDefault(level, message);
			} else {
				if (Bukkit.getConsoleSender() != null) { //This seems to only be an issue on legacy servers during startup
					String start = PREFIX.replace("%L", (displayLevel ? ChatColor.DARK_AQUA + "|" + level.getColorImplementation() + level : ""));
					Bukkit.getConsoleSender().sendMessage(start + level.getColorImplementation() + message);
				} else {
					Bukkit.getLogger().log(level.getLevel(), "[" + prefix + "] " + message);
				}
			}
		} else if (Platform.isSponge()) {
			Sponge.getServer().getConsole().sendMessage(Text.of(
					TextColors.DARK_AQUA, "[", TextColors.AQUA, prefix,
					(displayLevel ? Text.of(TextColors.DARK_AQUA, "|", level.getColorImplementation(), level) : Text.empty()),
					TextColors.DARK_AQUA, "] ", level.getColorImplementation(), message));
		} else {
			printDefault(level, message);
		}
	}

	private static void printDefault(LogLevel level, String message) {
		if (level == LogLevel.ERROR) {
			System.err.println(message);
		} else {
			System.out.println(message);
		}
	}

}
