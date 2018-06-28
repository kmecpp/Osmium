package com.kmecpp.osmium.api.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kmecpp.jlib.Validate;
import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.osmium.OsmiumData;

/**
 * The abstract superclass of all Osmium plugins.
 */
public abstract class OsmiumPlugin {

	private Class<? extends OsmiumPlugin> main = OsmiumData.getMainClass();
	private Plugin properties = main.getAnnotation(Plugin.class);
	private HashMap<Class<?>, Object> listeners = new HashMap<>();

	private final String LOG_MARKER = properties.name();

	//Effectively final variables
	private OsmiumPlugin plugin;
	private Logger logger;

	private Class<?> config;

	private HashSet<Class<?>> pluginClasses = new HashSet<>();

	public OsmiumPlugin() {
		Validate.notNull(properties, "Osmium plugins must be annotated with @OsmiumMeta");

		try {
			pluginClasses = new HashSet<>(Reflection.getClasses(new JarFile(new File(main.getProtectionDomain().getCodeSource().getLocation().toURI())), main.getPackage().getName()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		plugin = this;
		logger = LoggerFactory.getLogger(properties.name());
	}

	public Class<? extends OsmiumPlugin> getMainClass() {
		return main;
	}

	public HashSet<Class<?>> getPluginClasses() {
		return pluginClasses;
	}

	public HashMap<Class<?>, Object> getListeners() {
		return listeners;
	}

	public void enableEvents(Object listener) {

	}

	public Class<?> getConfig() {
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

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	//Meta
	public final String getName() {
		return properties.name();
	}

	public final String getVersion() {
		return properties.version();
	}

	public final String getDescription() {
		return properties.description();
	}

	public final String getUrl() {
		return properties.url();
	}

	public final String[] getAuthors() {
		return properties.authors();
	}

	public final String[] getDependencies() {
		return properties.dependencies();
	}

	//Logging
	public void debug(String message) {
		logger.debug(LOG_MARKER, message);
	}

	public void info(String message) {
		logger.info(LOG_MARKER, message);
	}

	public void warn(String message) {
		logger.warn(LOG_MARKER, message);
	}

	public void error(String message) {
		logger.error(LOG_MARKER, message);
	}

	public Logger logger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
