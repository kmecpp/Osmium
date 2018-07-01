package com.kmecpp.osmium.api.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;

import com.kmecpp.jlib.Validate;
import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.osmium.OsmiumLogger;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.event.events.EventInfo;
import com.kmecpp.osmium.api.platform.Platform;

/**
 * The abstract superclass of all Osmium plugins.
 */
public abstract class OsmiumPlugin {

	//	private final Class<? extends OsmiumPlugin> main = OsmiumProperties.getMainClass();
	//	private final OsmiumProperties osmiumProperties = new OsmiumProperties(this);
	private final Plugin properties = this.getClass().getAnnotation(Plugin.class);
	private final HashMap<Class<?>, Object> listeners = new HashMap<>();

	private final String LOG_MARKER = properties.name();

	//Effectively final variables
	private Object pluginImpl; //This field is set on instantiation using reflection
	private OsmiumPlugin plugin;
	private Logger logger;

	private Class<?> config;

	private HashSet<Class<?>> pluginClasses = new HashSet<>();

	public OsmiumPlugin() {
		Validate.notNull(properties, "Osmium plugins must be annotated with @OsmiumMeta");

		this.plugin = this;
		this.logger = LoggerFactory.getLogger(properties.name());
		//setupPlugin method is called immediately after construction via reflection
	}

	@SuppressWarnings("unused")
	private void setupPlugin(Object pluginImpl) throws Exception {
		this.pluginImpl = pluginImpl;

		JarFile jarFile = new JarFile(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
		pluginClasses = Reflection.getClasses(pluginImpl.getClass().getClassLoader(), jarFile, this.getClass().getPackage().getName());
	}

	void registerEvents() {
		for (Class<?> listener : plugin.getPluginClasses()) {
			if (!Reflection.isConcrete(listener)) {
				continue;
			}

			for (Method method : listener.getMethods()) {
				Listener annotation = method.getAnnotation(Listener.class);
				if (annotation != null) {
					if (method.getParameterCount() != 1) {
						plugin.error("Invalid listener method with signature: '" + method + "'");
					} else if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
						plugin.error("Invalid listener method with signature: '" + method + "'");
					} else {
						Class<Event> eventClass = Reflection.cast(method.getParameterTypes()[0]);
						EventInfo eventInfo = EventInfo.get(eventClass);

						Object listenerInstance;
						try {
							boolean contains = plugin.getListeners().containsKey(listener);
							listenerInstance = contains ? plugin.getListeners().get(listener) : listener.newInstance();
							if (!contains) {
								plugin.getListeners().put(listener, listenerInstance);
							}
						} catch (Exception e) {
							OsmiumLogger.error("Cannot instantiate " + listener.getName() + "! Listener classes without a default constructor must be enabled with: plugin.enableEvents(listener)");
							e.printStackTrace();
							break;
						}

						if (Platform.isBukkit()) {
							Class<? extends org.bukkit.event.Event> bukkitEventClass = eventInfo.getBukkitClass();
							EventPriority priority = (EventPriority) annotation.order().getSource();
							try {
								Constructor<?> eventWrapper = eventInfo.getBukkitImplementation().getConstructor(bukkitEventClass);
								Bukkit.getPluginManager().registerEvent(bukkitEventClass, (org.bukkit.event.Listener) asBukkitPlugin(), priority, (l, e) -> {
									if (bukkitEventClass.isAssignableFrom(e.getClass())) {
										try {
											method.invoke(listenerInstance, eventWrapper.newInstance(e));
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}, asBukkitPlugin(), true);
							} catch (Exception e) {
								e.printStackTrace();
								break;
							}
						} else if (Platform.isSponge()) {
							//TODO
						}
					}
				}
			}
		}
	}

	//	public Class<? extends OsmiumPlugin> getMainClass() {
	//		return this.getClass();
	//	}

	public HashSet<Class<?>> getPluginClasses() {
		return pluginClasses;
	}

	public HashMap<Class<?>, Object> getListeners() {
		return listeners;
	}

	public void enableEvents(Object listener) {
		listeners.put(listener.getClass(), listener);
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

	public SpongePlugin asSpongePlugin() {
		return (SpongePlugin) pluginImpl;
	}

	public BukkitPlugin asBukkitPlugin() {
		return (BukkitPlugin) pluginImpl;
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
