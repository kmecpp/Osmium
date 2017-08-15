package com.kmecpp.osmium.api.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kmecpp.jlib.Validate;
import com.kmecpp.osmium.OsmiumData;

/**
 * The abstract superclass of all Osmium plugins.
 */
public abstract class OsmiumPlugin {

	private static final String LOG_MARKER = getName();
	private static Class<? extends OsmiumPlugin> main = OsmiumData.getMainClass();
	private static PluginProperties properties = main.getAnnotation(PluginProperties.class);

	//Effectively final variables
	private static OsmiumPlugin plugin;
	private static Logger logger;

	private static Initializer initializer;
	private static Class<?> config;

	public OsmiumPlugin() {
		Validate.notNull(properties, "Osmium plugins must be annotated with @OsmiumMeta");

		plugin = this;
		logger = LoggerFactory.getLogger(properties.name());
	}

	public static Class<?> getConfig() {
		return config;
	}

	public void onLoad() {
	}

	public void preInit() {
	}

	public void init() {
	}

	public void postInit() {
	}

	public static OsmiumPlugin getPlugin() {
		return plugin;
	}

	//Meta
	public static final String getName() {
		return properties.name();
	}

	public static final String getVersion() {
		return properties.version();
	}

	public static final String getDescription() {
		return properties.description();
	}

	public static final String getUrl() {
		return properties.url();
	}

	public static final String[] getAuthors() {
		return properties.authors();
	}

	public static final String[] getDependencies() {
		return properties.dependencies();
	}

	public static final Initializer getInitializer() {
		return initializer;
	}

	public final void setInitializer(Initializer initializer) {
		OsmiumPlugin.initializer = initializer;
	}

	//Logging
	public static void debug(String message) {
		logger.debug(LOG_MARKER, message);
	}

	public static void info(String message) {
		logger.info(LOG_MARKER, message);
	}

	public static void warn(String message) {
		logger.warn(LOG_MARKER, message);
	}

	public static void error(String message) {
		logger.error(LOG_MARKER, message);
	}

	public static Logger logger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		OsmiumPlugin.logger = logger;
	}
}
