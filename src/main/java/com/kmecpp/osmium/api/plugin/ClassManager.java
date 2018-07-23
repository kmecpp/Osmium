package com.kmecpp.osmium.api.plugin;

import java.lang.reflect.Method;
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
import com.kmecpp.osmium.api.config.Configuration;
import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.event.EventInfo;
import com.kmecpp.osmium.api.event.Listener;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.util.Reflection;

public class ClassManager {

	private final OsmiumPlugin plugin;
	private final Class<?> mainClass;
	private final Class<?> mainClassImpl;
	private final HashSet<Class<?>> pluginClasses = new HashSet<Class<?>>();

	private final HashMap<Class<?>, Object> listeners = new HashMap<>();
	private final HashMap<Class<?>, Command> commands = new HashMap<>();

	protected ClassManager(OsmiumPlugin plugin, Object pluginImpl) throws Exception {
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
					OsmiumLogger.debug("SKIPPING: " + className);
					//Ignore classes depending on different platforms (TODO: COULD EASILY BREAK STUFF)
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

	public void enableEvents(Object listener) {
		listeners.put(listener.getClass(), listener);
	}

	protected void initializeHooks() {
		for (Class<?> cls : pluginClasses) {
			if (!Reflection.isConcrete(cls)) {
				continue;
			}
			onEnable(cls);
		}
	}

	private void onLoad(Class<?> cls) {
		OsmiumLogger.debug("Loading class: " + cls.getName());

		//CONFIGURATIONS
		if (cls.isAnnotationPresent(Configuration.class)) {
			Osmium.reloadConfig(cls);
			OsmiumLogger.debug("Loading configuration file: " + cls.getAnnotation(Configuration.class).path());
		}
	}

	private void onEnable(Class<?> cls) {
		OsmiumLogger.debug("Loading class: " + cls.getName());

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

		//LISTENERS
		for (Method method : cls.getMethods()) {
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
						boolean contains = listeners.containsKey(cls);
						listenerInstance = contains ? listeners.get(cls) : cls.newInstance();
						if (!contains) {
							listeners.put(cls, listenerInstance);
						}
					} catch (Exception e) {
						OsmiumLogger.error("Cannot instantiate " + cls.getName() + "! Listener classes without a default constructor must be enabled with: plugin.enableEvents(listener)");
						e.printStackTrace();
						break;
					}

					try {
						if (Platform.isBukkit()) {
							BukkitAccess.registerListener(plugin, eventInfo, annotation.order(), method, listenerInstance);
						} else if (Platform.isSponge()) {
							SpongeAccess.registerListener(plugin, eventInfo, annotation.order(), method, listenerInstance);
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
