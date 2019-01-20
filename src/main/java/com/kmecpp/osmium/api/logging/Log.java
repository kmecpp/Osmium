package com.kmecpp.osmium.api.logging;

public class Log {

	//	public static void debug(String message) {
	//		log(LogLevel.DEBUG, message);
	//	}
	//
	//	public static void info(String message) {
	//		log(LogLevel.INFO, message);
	//	}
	//
	//	public static void warn(String message) {
	//		log(LogLevel.WARN, message);
	//	}
	//
	//	public static void error(String message) {
	//		log(LogLevel.ERROR, message);
	//	}
	//
	//	public static void log(LogLevel level, String message) {
	//		String className = Thread.currentThread().getStackTrace()[3].getClassName();
	//
	//		boolean success = false;
	//		try {
	//			OsmiumPlugin plugin = Osmium.getPlugin(Class.forName(className));
	//			if (plugin != null) {
	//				logRaw(plugin.getLogger(), level, plugin.getName(), message);
	//				success = true;
	//			}
	//		} catch (Exception e) {
	//		}
	//
	//		if (!success) {
	//			logRaw(LoggerFactory.getLogger(className), level, className, message);
	//		}
	//	}
	//
	//	public static void logRaw(Logger logger, LogLevel level, String prefix, String message) {
	//		if (level.isDebug() && CoreOsmiumConfiguration.debug) {
	//			logger.debug(message);
	//		} else if (level.isInfo()) {
	//			logger.info(message);
	//		} else if (level.isWarn()) {
	//			logger.warn(message);
	//		} else if (level.isError()) {
	//			logger.error(message);
	//		}
	//	}
	//
	//	//	public static OsmiumPlugin getPlugin() {
	//	//		try {
	//	//			return Osmium.getPlugin(Class.forName(Thread.currentThread().getStackTrace()[3].getClassName()));
	//	//		} catch (ClassNotFoundException e) {
	//	//			return null;
	//	//		}
	//	//	}

}
