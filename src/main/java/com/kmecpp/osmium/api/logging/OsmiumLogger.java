package com.kmecpp.osmium.api.logging;

import com.kmecpp.osmium.AppInfo;

public class OsmiumLogger {

	private static final OsmiumPluginLogger LOGGER = new OsmiumPluginLogger(AppInfo.NAME);

	public static final void debug(String message) {
		LOGGER.debug(message);
	}

	public static final void info(String message) {
		LOGGER.info(message);
	}

	public static final void warn(String message) {
		LOGGER.warn(message);
	}

	public static final void error(String message) {
		LOGGER.error(message);
	}

}
