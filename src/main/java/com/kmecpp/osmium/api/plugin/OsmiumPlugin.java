package com.kmecpp.osmium.api.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kmecpp.jlib.Validate;
import com.kmecpp.osmium.OsmiumData;

public abstract class OsmiumPlugin {

	public static final String NAME = "";

	//Effectively final variables
	private static OsmiumPlugin plugin;
	private static Plugin meta;
	private static Logger logger;

	private static Initializer initializer;
	private static Class<?> config;

	public OsmiumPlugin() {
		plugin = this;
		meta = this.getClass().getAnnotation(Plugin.class);
		logger = LoggerFactory.getLogger(OsmiumData.getMainClass().getName());
		Validate.notNull(meta, "Osmium plugins must be annotated with @OsmiumMeta");
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

	public static final String getName() {
		return meta.name();
	}

	public static final String getVersion() {
		return meta.version();
	}

	public static final String getDescription() {
		return meta.description();
	}

	public static final String getUrl() {
		return meta.url();
	}

	public static final String[] getAuthors() {
		return meta.authors();
	}

	public static final String[] getDependencies() {
		return meta.dependencies();
	}

	public static final Initializer getInitializer() {
		return initializer;
	}

	public final void setInitializer(Initializer initializer) {
		OsmiumPlugin.initializer = initializer;
	}

	public static void debug(String message) {
		logger.debug(message);
	}

	public static void info(String message) {
		logger.info(message);
	}

	public static void warn(String message) {
		logger.warn(message);
	}

	public static void error(String message) {
		logger.error(message);
	}

	public static Logger logger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		OsmiumPlugin.logger = logger;
	}
}
