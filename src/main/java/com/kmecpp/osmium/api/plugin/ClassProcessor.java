package com.kmecpp.osmium.api.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandProperties;
import com.kmecpp.osmium.api.config.ConfigProperties;
import com.kmecpp.osmium.api.database.DBTable;
import com.kmecpp.osmium.api.database.Database;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Persistent;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.tasks.Schedule;
import com.kmecpp.osmium.api.util.Reflection;

public class ClassProcessor {

	private final OsmiumPlugin plugin;
	private final Class<?> mainClass;
	private final Class<?> mainClassImpl;
	private final HashSet<Class<?>> pluginClasses = new HashSet<Class<?>>();

	private final HashMap<Class<?>, Object> classInstances = new HashMap<>();
	private final HashMap<Class<?>, Command> commands = new HashMap<>();

	protected ClassProcessor(OsmiumPlugin plugin, Object pluginImpl) throws Exception {
		this.plugin = plugin;
		this.mainClass = plugin.getClass();
		this.mainClassImpl = pluginImpl.getClass();

		ClassLoader classLoader = mainClassImpl.getClassLoader();

		JarFile jarFile = Directory.getJarFile(mainClass);
		String packageName = mainClass.getPackage().getName();

		Enumeration<JarEntry> entry = jarFile.entries();
		while (entry.hasMoreElements()) {
			String name = entry.nextElement().getName().replace("/", ".");
			if (name.startsWith(packageName) && name.endsWith(".class")) {
				String className = name.substring(0, name.length() - 6);
				try {
					Class<?> cls = classLoader.loadClass(className);
					cls.getDeclaredMethods(); //Verify that return types exist
					onLoad(cls);
					pluginClasses.add(cls);
				} catch (ClassNotFoundException | NoClassDefFoundError e) {
					if (e.getMessage().contains("org/spongepowered") || e.getMessage().contains("org/bukkit")) {
						OsmiumLogger.debug("SKIPPING: " + className);
					} else {
						OsmiumLogger.error("Could not load class: " + className);
						e.printStackTrace();
					}
					//Ignore classes depending on different platforms (TODO: THIS COULD EASILY BREAK STUFF)
				} catch (Exception e) {
					OsmiumLogger.error("Failed to load plugin class: " + className);
					e.printStackTrace();
				}
			}
		}
		jarFile.close();
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public Class<?> getMainClassImpl() {
		return mainClassImpl;
	}

	public HashSet<Class<?>> getPluginClasses() {
		return pluginClasses;
	}

	public HashMap<Class<?>, Object> getClassInstances() {
		return classInstances;
	}

	public void enableEvents(Object listener) {
		classInstances.put(listener.getClass(), listener);
	}

	protected void initializeHooks() {
		for (Class<?> cls : pluginClasses) {
			if (!Reflection.isConcrete(cls)) {
				continue;
			}
			try {
				onEnable(cls);
			} catch (Exception e) {
				OsmiumLogger.error("Failed to enable class: " + cls.getName());
				e.printStackTrace();
			}
		}
	}

	private void onLoad(Class<?> cls) {
		OsmiumLogger.debug("Loading class: " + cls.getName());

		//CONFIGURATIONS
		ConfigProperties configuration = cls.getAnnotation(ConfigProperties.class);
		if (configuration != null) {
			Osmium.reloadConfig(cls);
			OsmiumLogger.debug("Loading configuration file: " + configuration.path());
		}

		//DATABASE TABLES
		DBTable table = cls.getAnnotation(DBTable.class);
		if (table != null) {
			OsmiumLogger.debug("Initializing database table: " + table.name());
			Osmium.getDatabase(plugin).createTable(cls);
			Database.isSerializable(cls);
		}

		//PERSISTENT FIELDS
		for (Field field : cls.getDeclaredFields()) {
			Persistent persistentAnnotation = field.getAnnotation(Persistent.class);
			if (persistentAnnotation != null) {
				if (!Modifier.isStatic(field.getModifiers())) {
					OsmiumLogger.error("Fields annotated with @" + Persistent.class.getSimpleName() + " must be static! Incorrect: " + field);
					continue;
				}

				field.setAccessible(true);
				plugin.getPersistentData().addField(field);
			}
		}
	}

	private void onEnable(Class<?> cls) {
		OsmiumLogger.debug("Initializing class: " + cls.getName());

		//COMMANDS
		if (cls.isAnnotationPresent(CommandProperties.class)) {
			if (!Command.class.isAssignableFrom(cls)) {
				OsmiumLogger.warn("Class is annotated with @" + CommandProperties.class.getSimpleName() + " but does not extend " + Command.class.getSimpleName() + ": " + cls);
				return;
			}

			Command command;
			try {
				command = (Command) cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				OsmiumLogger.warn("Cannot cannot be initialized! Class must have a default constructor!");
				return;
			}

			commands.put(cls, command);

			if (command.getAliases().length == 0) {
				OsmiumLogger.warn("Command does not have any aliases and will not be registered: " + cls);
				return;
			}

			if (Platform.isBukkit()) {
				BukkitAccess.registerCommand(plugin, command);
			} else if (Platform.isSponge()) {
				SpongeAccess.registerCommand(plugin, command);
			}
		}

		for (Method method : cls.getDeclaredMethods()) {
			Schedule scheduleAnnotation = method.getAnnotation(Schedule.class);
			Listener listenerAnnotation = method.getAnnotation(Listener.class);
			if (scheduleAnnotation == null && listenerAnnotation == null) {
				continue;
			}

			Object instance;
			try {
				boolean contains = classInstances.containsKey(cls); //DONE THIS WAY BECAUSE LISTENER MUST BE FINAL
				instance = contains ? classInstances.get(cls) : cls.newInstance();
				if (!contains) {
					classInstances.put(cls, instance);
				}
			} catch (Exception e) {
				OsmiumLogger.error("Cannot instantiate " + cls.getName() + "! Task and listener classes without a default constructor must be enabled with: plugin.enableEvents(listener)");
				e.printStackTrace();
				break;
			}

			//TASKS
			if (scheduleAnnotation != null) {
				Osmium.getTask(plugin)
						.setAsync(scheduleAnnotation.async())
						.setDelay(scheduleAnnotation.delay() * scheduleAnnotation.unit().getTickValue())
						.setInterval(scheduleAnnotation.interval() * scheduleAnnotation.unit().getTickValue())
						.setExecutor((t) -> {
							try {
								method.invoke(instance);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
						})
						.start();
			}

			//LISTENERS
			if (listenerAnnotation != null) {
				if (method.getParameterCount() != 1) {
					plugin.error("Invalid listener method with signature: '" + method + "'");
				} else if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
					plugin.error("Invalid listener method with signature: '" + method + "'");
				} else {
					Class<? extends Event> eventClass = Reflection.cast(method.getParameterTypes()[0]);
					EventInfo eventInfo = EventInfo.get(eventClass);

					if (eventInfo == null) {
						OsmiumLogger.error("Osmium event class has no registration: " + eventClass);
						continue;
					}

					if (eventInfo.isOsmiumEvent()) {
						Osmium.getEventManager().registerListener(eventInfo.getImplementation(), instance, method); //Register implementation class for Osmium
					} else {
						try {
							if (Platform.isBukkit()) {
								BukkitAccess.registerListener(plugin, eventInfo, listenerAnnotation.order(), method, instance);
							} else if (Platform.isSponge()) {
								SpongeAccess.registerListener(plugin, eventInfo, listenerAnnotation.order(), method, instance);
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
				}
			}
		}
	}

}
