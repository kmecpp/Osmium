package com.kmecpp.osmium.api.plugin;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.common.SpongeImpl;

import com.google.common.base.Preconditions;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.config.PluginConfigTypeData;
import com.kmecpp.osmium.api.database.MySQLDatabase;
import com.kmecpp.osmium.api.database.SQLiteDatabase;
import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.api.event.events.osmium.PluginReloadEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.logging.OsmiumPluginLogger;
import com.kmecpp.osmium.api.persistence.PersistentPluginData;
import com.kmecpp.osmium.api.tasks.CountdownTask;
import com.kmecpp.osmium.api.tasks.OsmiumTask;
import com.kmecpp.osmium.api.util.Reflection;

/**
 * The abstract superclass of all Osmium plugins.
 */
public abstract class OsmiumPlugin {

	private final Plugin properties = this.getClass().getAnnotation(Plugin.class);
	private final SQLiteDatabase sqliteDatabase = new SQLiteDatabase(this);
	private final MySQLDatabase mysqlDatabase = new MySQLDatabase(this);

	//Effectively final variables
	private Object pluginImplementation; //This field is set on instantiation using reflection
	private Object metricsImplementation;
	private PersistentPluginData persistentData;
	private OsmiumPluginLogger logger = new OsmiumPluginLogger(properties.name());
	private Path dataFolder;

	private ClassProcessor classProcessor;
	private PluginConfigTypeData configTypeData;

	boolean startComplete;
	boolean startError;

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
	private final void setupPlugin(Object pluginImpl, PluginConfigTypeData configTypeData) throws Exception {
		this.pluginImplementation = pluginImpl;
		if (Platform.isBukkit()) {
			this.dataFolder = this.<JavaPlugin> getSource().getDataFolder().toPath();
		} else if (Platform.isSponge()) {
			this.dataFolder = SpongeImpl.getPluginConfigDir() != null ? SpongeImpl.getPluginConfigDir().resolve(properties.name()) : Paths.get("");
		} else if (Platform.isBungeeCord()) {
			this.dataFolder = this.<net.md_5.bungee.api.plugin.Plugin> getSource().getDataFolder().toPath();
		}
		this.persistentData = new PersistentPluginData(this);
		this.classProcessor = new ClassProcessor(this, pluginImpl); //This loads the plugin's configs and persistent data
		this.configTypeData = configTypeData;
		this.classProcessor.provideInstance(this); //Always register main class. Avoids creating a second instance later
		Files.createDirectories(this.dataFolder); //Always create a folder for the plugin. Maybe we only want to do this if necessary?

		onConstruct();

		//		if (!database.getTables().isEmpty()) {
		//			database.start();
		//		}
	}

	public void saveResource(String path) {
		saveResource(path, false);
	}

	public void saveResource(String path, boolean replace) {
		Path savePath = getFolder().resolve(path);
		try {
			if (Platform.isBukkit()) {
				this.<JavaPlugin> getSource().saveResource(path, replace);
			} else if (Platform.isSponge()) {
				this.<org.spongepowered.api.plugin.PluginContainer> getSource().getAsset(path).get().copyToFile(savePath, replace);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
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

	public void onConstruct() {
	}

	public void onLoad() {
	}

	public void onPreInit() {
	}

	public void onInit() {
	}

	public void onPostInit() {
	}

	public void onServerStarted() {
	}

	public void onReload(PluginReloadEvent e) {
	}

	/**
	 * Called every time onReload and onEnable is called
	 */
	public void onRefresh(PluginRefreshEvent e) {
	}

	//	public void onFinalize() {
	//	}

	public void onDisable() {
	}

	public boolean isStartComplete() {
		return startComplete;
	}

	public <T> T provideInstance(T instance) {
		classProcessor.provideInstance(instance);
		return instance;
	}
	
	public <T> T getInstance(Class<T> cls) {
		return classProcessor.getInstance(cls);
	}

	public void enableMetrics() {
		Osmium.getMetrics().register(this);
	}

	public boolean isMetricsEnabled() {
		return Osmium.getMetrics().isEnabled(this);
	}

	public void savePersistentData() {
		persistentData.save();
		//TODO: SAVE PLAYER DB RECORDS
		//		database.getTables().forEach(TableProperties::get);
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
	public <T> T getSource() {
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

	public SQLiteDatabase getSQLiteDatabase() {
		return sqliteDatabase;
	}

	public MySQLDatabase getMySQLDatabase() {
		return mysqlDatabase;
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

	public PluginConfigTypeData getConfigTypeData() {
		return configTypeData;
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

	public Command registerCommand(String name, String... aliases) {
		return Osmium.getCommandManager().register(this, new Command(name, aliases));
	}

	@SuppressWarnings("unchecked")
	public <T> HashSet<Class<? extends T>> getSubclasses(Class<T> cls) {
		HashSet<Class<? extends T>> classes = new HashSet<>();
		for (Class<?> c : this.classProcessor.getPluginClasses()) {
			if (Reflection.isConcrete(c) && cls.isAssignableFrom(c) && !cls.equals(c)) {
				classes.add((Class<T>) c);
			}
		}
		return classes;
	}

}
