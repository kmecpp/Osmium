package com.kmecpp.osmium.api.plugin;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.common.SpongeImpl;

import com.google.common.base.Preconditions;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandBase;
import com.kmecpp.osmium.api.database.Database;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.logging.OsmiumPluginLogger;
import com.kmecpp.osmium.api.persistence.PersistentPluginData;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.tasks.CountdownTask;
import com.kmecpp.osmium.api.tasks.OsmiumTask;
import com.kmecpp.osmium.api.util.Reflection;

/**
 * The abstract superclass of all Osmium plugins.
 */
public abstract class OsmiumPlugin {

	private final Plugin properties = this.getClass().getAnnotation(Plugin.class);
	private final Database database = new Database(this);

	//Effectively final variables
	private Object pluginImplementation; //This field is set on instantiation using reflection
	private Object metricsImplementation;
	private PersistentPluginData persistentData;
	private OsmiumPluginLogger logger = new OsmiumPluginLogger(properties.name());
	private Path dataFolder;

	private ClassProcessor classProcessor;

	public OsmiumPlugin() {
		Preconditions.checkNotNull(properties, "Osmium plugins must be annotated with @OsmiumMeta");

		//setupPlugin method is called immediately after construction via reflection

		for (Field field : this.getClass().getDeclaredFields()) {
			PluginInstance pluginInstance = field.getAnnotation(PluginInstance.class);
			if (pluginInstance != null) {
				if (field.getType() == this.getClass()) {
					Reflection.setField(null, field, this);
					break;
				} else {
					OsmiumLogger.warn("Invalid field annotated with @" + PluginInstance.class.getSimpleName() + ": " + field);
				}
			}
		}
	}

	/*
	 * This method is called by the Osmium plugin immediately following
	 * construction to setup the plugin. This is not done in the constructor in
	 * order to hide these steps from the end user
	 */
	@SuppressWarnings("unused")
	private final void setupPlugin(Object pluginImpl) throws Exception {
		this.pluginImplementation = pluginImpl;
		if (Platform.isBukkit()) {
			this.dataFolder = this.<JavaPlugin> getPluginImplementation().getDataFolder().toPath();
		} else if (Platform.isSponge()) {
			dataFolder = SpongeImpl.getPluginConfigDir() != null ? SpongeImpl.getPluginConfigDir().resolve(properties.name()) : Paths.get("");
		}
		this.persistentData = new PersistentPluginData(this);
		this.classProcessor = new ClassProcessor(this, pluginImpl); //This loads the plugin's configs and persistent data

		//		if (!database.getTables().isEmpty()) {
		//			database.start();
		//		}
	}

	public ClassProcessor getClassProcessor() {
		return classProcessor;
	}

	public PersistentPluginData getPersistentData() {
		return persistentData;
	}

	//	public void setDefaultConfig(Class<?> configClass) {
	//		this.config = configClass;
	//	}
	//
	//	public Class<?> getConfig() {
	//		return config;
	//	}

	public void onLoad() {
	}

	public void onPreInit() {
	}

	public void onInit() {
	}

	public void onPostInit() {
	}

	public void onReload() {
	}

	public void onDisable() {
	}

	public <T> T provideInstance(T instance) {
		classProcessor.provideInstance(instance);
		return instance;
	}

	public void enableMetrics() {
		Osmium.getMetrics().register(this);
	}

	public boolean isMetricsEnabled() {
		return Osmium.getMetrics().isEnabled(this);
	}

	public void saveData() {
		persistentData.save();
	}

	//	public void enableMetrics() {
	//		if (Platform.isBukkit()) {
	//			this.metricsImplementation = new BukkitMetrics(getPluginImplementation());
	//		} else if (Platform.isSponge()) {
	//			PluginContainer plugin = getPluginImplementation();
	//			this.metricsImplementation = Reflection.newInstance(SpongeMetrics.class, plugin, plugin.getLogger(), Sponge.getConfigManager().getSharedConfig(plugin).getDirectory());
	//			//			Reflection.newInstance(org.bstats.sponge.Metrics.class);
	//			//			this.metricsImplementation = org.bstats.sponge.Metrics.
	//		}
	//	}

	//	public SpongePlugin asSpongePlugin() {
	//		return (SpongePlugin) pluginImpl;
	//	}
	//
	//	public BukkitPlugin asBukkitPlugin() {
	//		return (BukkitPlugin) pluginImpl;
	//	}

	@SuppressWarnings("unchecked")
	public <T> T getPluginImplementation() {
		return (T) pluginImplementation;
	}

	@SuppressWarnings("unchecked")
	public <T> T getMetricsImplementation() {
		return (T) metricsImplementation;
	}

	//Meta
	public final String getId() {
		return properties.name().toLowerCase().replace(' ', '-');
	}

	public final String getName() {
		return properties.name();
	}

	public final String getVersion() {
		return properties.version();
	}

	public final boolean hasDescription() {
		return !properties.description().isEmpty();
	}

	public final String getDescription() {
		return properties.description();
	}

	public final boolean hasWebsite() {
		return !properties.url().isEmpty();
	}

	public final String getWebsite() {
		return properties.url();
	}

	public final boolean hasAuthors() {
		return properties.authors().length > 0;
	}

	public final String[] getAuthors() {
		return properties.authors();
	}

	public final boolean hasDependencies() {
		return properties.dependencies().length > 0;
	}

	public final String[] getDependencies() {
		return properties.dependencies();
	}

	//Logging
	public void debug(String message) {
		logger.debug(message);
	}

	public void info(String message) {
		logger.info(message);
	}

	public void warn(String message) {
		logger.warn(message);
	}

	public void error(String message) {
		logger.error(message);
	}

	//	public Logger getLogger() {
	//		return logger;
	//	}
	//
	//	public void setLogger(Logger logger) {
	//		this.logger = logger;
	//	}

	public Database getDatabase() {
		return database;
	}

	//	public void disable() {
	//		if (Platform.isBukkit()) {
	//
	//		}
	//	}

	public Path getFolder() {
		return dataFolder;
		//		if (Platform.isBukkit() && Platform.isSponge()) {
		//			return Paths.get("");//TODO: Find a better way to handle tests
		//		}
		//
		//		if (Platform.isBukkit()) {
		//			return ((JavaPlugin) pluginImplementation).getDataFolder().toPath();
		//		} else if (Platform.isSponge()) {
		//			//			return Sponge.getGame().getConfigManager().getPluginConfig(pluginImplementation).getDirectory();
		//			return Paths.get("Osmium");
		//		} else {
		//			return null;
		//		}
	}

	public OsmiumTask getTask() {
		return new OsmiumTask(this);
	}

	public CountdownTask countdown(int count) {
		return new CountdownTask(this, count);
	}

	public final void reload() {
		Osmium.reloadPlugin(this);
	}

	public CommandBase registerCommand(String name, String... aliases) {
		return Osmium.getCommandManager().register(this, new Command(name, aliases));
	}

}
