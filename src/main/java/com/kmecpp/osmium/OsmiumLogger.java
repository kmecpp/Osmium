package com.kmecpp.osmium;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsmiumLogger {

	private static Logger logger = LoggerFactory.getLogger(Osmium.OSMIUM);

	public static final void log(String message) {
		logger.info(message);
	}

	public static final void debug(String message) {
		logger.debug(message);
	}

	public static final void info(String message) {
		logger.info(message);
	}

	public static final void warn(String message) {
		logger.warn(message);
	}

	public static final void error(String message) {
		logger.error(message);
	}

}
