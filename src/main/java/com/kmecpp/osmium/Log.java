package com.kmecpp.osmium;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class Log {

	public static void debug(String message) {
		getPlugin().debug(message);
	}

	public static void info(String message) {
		getPlugin().info(message);
	}

	public static void warn(String message) {
		getPlugin().warn(message);
	}

	public static void error(String message) {
		getPlugin().error(message);
	}

	public static OsmiumPlugin getPlugin() {
		try {
			return Osmium.getPlugin(Class.forName(Thread.currentThread().getStackTrace()[3].getClassName()));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
