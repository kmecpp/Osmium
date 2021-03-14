package com.kmecpp.osmium.api.logging;

import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.core.OsmiumCoreConfig;

import net.md_5.bungee.BungeeCord;

public class OsmiumPluginLogger {

	private final String name;
	private final Logger logger;
	//	private final String prefix;

	public OsmiumPluginLogger(String name) {
		this.name = name;
		this.logger = LoggerFactory.getLogger(name);
		//		this.PREFIX = Chat.DARK_AQUA + "[" + Chat.AQUA + NAME + "%L" + Chat.DARK_AQUA + "] ";
	}

	public final void debug(String message) {
		if (OsmiumCoreConfig.coloredConsole) {
			log(LogLevel.DEBUG, this.name, message);
		} else {
			logger.debug(message);
		}
	}

	public final void info(String message) {
		if (OsmiumCoreConfig.coloredConsole) {
			log(LogLevel.INFO, this.name, message);
		} else {
			logger.info(message);
		}
	}

	public final void warn(String message) {
		if (OsmiumCoreConfig.coloredConsole) {
			log(LogLevel.WARN, this.name, message);
		} else {
			logger.warn(message);
		}
	}

	public final void error(String message) {
		if (OsmiumCoreConfig.coloredConsole) {
			log(LogLevel.ERROR, this.name, message);
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
	private void log(LogLevel level, String prefix, String message) {
		if (level == LogLevel.DEBUG && !OsmiumCoreConfig.debug) {
			return;
		}

		message = Chat.strip(message);

		if (Platform.isBukkit()) {
			if (Bukkit.getServer() == null) {
				printDefault(level, message);
			} else {
				//				if (Bukkit.getConsoleSender() != null) { //This seems to only be an issue on legacy servers during startup
				//					//					String start = PREFIX.replace("%L", (displayLevel ? ChatColor.DARK_AQUA + "|" + level.getColorImplementation() + level : ""));
				//					//					Bukkit.getConsoleSender().sendMessage(start + level.getColorImplementation() + message);
				//					Bukkit.getLogger().log(level.getLevel(), level.getColor().getAnsi() + "[" + prefix + "] " + message + Chat.RESET.getAnsi());
				//				} else {
				//					Bukkit.getLogger().log(level.getLevel(), "[" + prefix + "] " + message);
				//				}
				Bukkit.getLogger().log(level.getLevel(), Chat.DARK_AQUA.ansi()
						+ "[" + Chat.AQUA.ansi() + prefix + Chat.DARK_AQUA.ansi() + "] " + level.getColor().ansi() + message + Chat.RESET.ansi());
			}
		} else if (Platform.isSponge()) {
			//TODO: Rewrite to use ANSI
			boolean displayLevel = level != LogLevel.DEBUG && level != LogLevel.INFO;
			Sponge.getServer().getConsole().sendMessage(Text.of(
					TextColors.DARK_AQUA, "[", TextColors.AQUA, prefix,
					(displayLevel ? Text.of(TextColors.DARK_AQUA, "|", level.getColor(), level) : Text.empty()),
					TextColors.DARK_AQUA, "] ", level.getColor(), message));
		} else if (Platform.isProxy()) {
			BungeeCord.getInstance().getLogger().log(level.getLevel(),
					Chat.DARK_AQUA.ansi() + "[" + Chat.AQUA.ansi() + prefix + Chat.DARK_AQUA.ansi() + "] " + level.getColor().ansi() + message + Chat.RESET.ansi());
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
