package com.kmecpp.osmium.api.plugin;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;

import com.kmecpp.jlib.Validate;
import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.osmium.api.platform.Platform;

/**
 * The abstract superclass of all Osmium plugins.
 */
public abstract class OsmiumPlugin {

	//	private final Class<? extends OsmiumPlugin> main = OsmiumProperties.getMainClass();
	//	private final OsmiumProperties osmiumProperties = new OsmiumProperties(this);
	private final Plugin properties = this.getClass().getAnnotation(Plugin.class);

	private final String LOG_MARKER = properties.name();

	//Effectively final variables
	private OsmiumPlugin plugin;
	private Object pluginImpl; //This field is set on instantiation using reflection
	private Logger logger;

	private Class<?> config;

	private ClassManager classManager;

	//	private HashSet<Class<?>> pluginClasses = new HashSet<>();

	public OsmiumPlugin() {
		Validate.notNull(properties, "Osmium plugins must be annotated with @OsmiumMeta");

		this.plugin = this;
		this.logger = LoggerFactory.getLogger(properties.name());
		//setupPlugin method is called immediately after construction via reflection

		for (Field field : this.getClass().getDeclaredFields()) {
			PluginInstance pluginInstance = field.getAnnotation(PluginInstance.class);
			if (pluginInstance != null) {
				if (pluginInstance.getClass() == this.getClass()) {
					Reflection.setField(this, field, this);
					break;
				} else {
					warn("Invalid field annotated with " + PluginInstance.class.getSimpleName() + ": " + field);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void setupPlugin(Object pluginImpl) throws Exception {
		this.pluginImpl = pluginImpl;
		this.classManager = new ClassManager(this, pluginImpl.getClass());
	}

	//	public Class<? extends OsmiumPlugin> getMainClass() {
	//		return this.getClass();
	//	}

	//	public HashSet<Class<?>> getPluginClasses() {
	//		return pluginClasses;
	//	}

	public ClassManager getClassManager() {
		return classManager;
	}

	public void setDefaultConfig(Class<?> configClass) {
		this.config = configClass;
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
	
	public void onDisable() {
	}

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public SpongePlugin asSpongePlugin() {
		return (SpongePlugin) pluginImpl;
	}

	public BukkitPlugin asBukkitPlugin() {
		return (BukkitPlugin) pluginImpl;
	}

	@SuppressWarnings("unchecked")
	public <T> T getPluginImplementation() {
		return (T) pluginImpl;
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

	public Path getPluginFolder() {
		if (Platform.isSponge()) {
			return Sponge.getGame().getConfigManager().getPluginConfig(pluginImpl).getDirectory();
		} else if (Platform.isBukkit()) {
			return Paths.get(((JavaPlugin) pluginImpl).getDataFolder().toURI());
		}
		return null;
	}

}
