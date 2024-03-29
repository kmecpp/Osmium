package com.kmecpp.osmium.api.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.config.PluginConfigTypeData;
import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.IOUtil;
import com.kmecpp.osmium.platform.BukkitAccess;
import com.kmecpp.osmium.platform.BungeeAccess;
import com.kmecpp.osmium.platform.SpongeAccess;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginRefreshEvent;

public class PluginLoader {

	private final HashMap<String, OsmiumPlugin> pluginFiles = new HashMap<>();
	private final HashMap<Class<? extends OsmiumPlugin>, OsmiumPlugin> plugins = new HashMap<>();
	private final HashMap<String, Path> pluginPaths = new HashMap<>(); //Map Plugin names to jar Path (this data is never cleared)

	private final HashMap<Class<?>, OsmiumPlugin> externalClasses = new HashMap<>();

	public OsmiumPlugin createOsmiumPlugin(Object pluginImpl) {
		try {
			//			String[] lines = IOUtil.readLines(pluginImpl.getClass().getResource("/osmium.properties")); //Weird sponge bug. Doesn't work anymore
			final JarFile jar = Directory.getJarFile(pluginImpl.getClass());
			final Path jarPath = new File(Directory.getJarFilePath(pluginImpl.getClass())).toPath();

			final String[] lines = IOUtil.readLines(jar.getInputStream(jar.getEntry("osmium.properties")));
			try {
				String mainClassName = lines[0].split(":")[1].trim();
				ClassLoader pluginClassLoader = pluginImpl.getClass().getClassLoader();
				//Note: Class loaders are parent-first so it's difficult to override class loading like this
				//				URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(jar.getName()).toURI().toURL() }, pluginImpl.getClass().getClassLoader());
				//				URLClassLoader classLoader = new URLClassLoader(new URL[] { new File(jar.getName()).toURI().toURL() });
				//				System.out.println("TEST: " + Class.forName(OsmiumPlugin.class.getName(), true, classLoader).getClassLoader());
				/*
				 * - Maybe its because the class isn't findable by the Plugin class loader b/c its not in the plugins folder?
				 */

				//				Class<OsmiumPlugin> osmiumPluginClass = Reflection.cast(Class.forName(OsmiumPlugin.class.getName(), true, classLoader));

				Class<? extends OsmiumPlugin> main;
				try {
					main = Class.forName(mainClassName, false, pluginClassLoader).asSubclass(OsmiumPlugin.class);
					//					main = classLoader.loadClass(mainClassName).asSubclass(OsmiumPlugin.class);
				} catch (ClassCastException e) {
					OsmiumLogger.error("Failed to load plugin: " + mainClassName + ". Does this class extend " + OsmiumPlugin.class.getSimpleName() + "?");
					throw new RuntimeException(e);
				}

				OsmiumPlugin plugin = main.newInstance();
				OsmiumLogger.info("Loading plugin: " + plugin.getName() + " v" + plugin.getVersion());
				pluginPaths.put(plugin.getName(), jarPath);

				ArrayList<String> configTypes = new ArrayList<>();
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().startsWith("CONFIG_TYPES")) {
						for (String line : IOUtil.readLines(jar.getInputStream(entry))) {
							configTypes.add(line);
						}
					}
				}
				PluginConfigTypeData configTypeData = PluginConfigTypeData.parse(plugin, configTypes);

				for (Field field : plugin.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(PluginInstance.class)
							&& Modifier.isStatic(field.getModifiers())
							&& OsmiumPlugin.class.isAssignableFrom(field.getType())) {
						field.setAccessible(true);
						field.set(null, plugin);
					}
				}

				String jarFilePath = jar.getName();
				OsmiumLogger.debug("File location: " + jarFilePath);

				plugins.put(main, plugin);
				pluginFiles.put(jarFilePath, plugin);

				Method method = OsmiumPlugin.class.getDeclaredMethod("setupPlugin", Object.class, PluginConfigTypeData.class);
				method.setAccessible(true);
				method.invoke(plugin, pluginImpl, configTypeData);
				//				Reflection.invokeMethod(OsmiumPlugin.class, plugin, "setupPlugin", pluginImpl);//Setup plugin after everything else

				//				OsmiumLogger.info("Successfully loaded Osmium plugin: " + plugin.getName() + " v" + plugin.getVersion());

				return plugin;
			} catch (InvocationTargetException e) {
				//				e.getTargetException().printStackTrace();
				throw new RuntimeException(e);
			} catch (Exception e) {
				OsmiumLogger.error("Could not load Osmium plugin: " + lines[1].split(":")[1].trim());
				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			OsmiumLogger.error("Could not load Osmium plugin. Failed to read osmium.properties from jar: " + pluginImpl.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public void onLoad(OsmiumPlugin plugin) {
		try {
			plugin.getClassProcessor().loadAll();
			plugin.onLoad();
		} catch (Throwable t) {
			catchStartError(plugin, t);
		}
	}

	public void onPreInit(OsmiumPlugin plugin) {
		try {
			plugin.onPreInit();
		} catch (Throwable t) {
			catchStartError(plugin, t);
		}
	}

	public void onInit(OsmiumPlugin plugin) {
		try {
			plugin.getClassProcessor().initializeClasses();
		} catch (Throwable t) {
			catchStartError(plugin, t);
		}

		try {
			plugin.onInit();
		} catch (Throwable t) {
			catchStartError(plugin, t);
		}

		try {
			plugin.getClassProcessor().createDatabaseTables();
		} catch (Throwable t) {
			catchStartError(plugin, t);
		}
	}

	public void onPostInit(OsmiumPlugin plugin) {
		try {
			plugin.onPostInit();
		} catch (Throwable t) {
			catchStartError(plugin, t);
		}

		PluginRefreshEvent refreshEvent = new OsmiumPluginRefreshEvent(plugin, true);
		plugin.onRefresh(refreshEvent);
		Osmium.getEventManager().callEvent(refreshEvent);
		plugin.startComplete = true;
	}

	public void onDisable(OsmiumPlugin plugin) {
		if (plugin != null) {
			plugin.savePersistentData();
			plugin.onDisable();
		}
	}

	private void catchStartError(OsmiumPlugin plugin, Throwable t) {
		t.printStackTrace();
		plugin.startError = true;
	}

	public void restartPlugin(OsmiumPlugin oldInstance) {
		unloadPlugin(oldInstance);
		loadPlugin(new File(Directory.getJarFilePath(oldInstance.getClass())));
	}

	public void loadPlugin(File jarFile) {
		OsmiumLogger.info("Loading plugin: " + jarFile.getName());

		OsmiumPlugin plugin;
		if (Platform.isBukkit()) {
			plugin = BukkitAccess.loadPlugin(jarFile);
		} else if (Platform.isSponge()) {
			plugin = SpongeAccess.loadPlugin(jarFile);
		} else if (Platform.isBungeeCord()) {
			plugin = BungeeAccess.loadPlugin(jarFile);
		} else {
			throw new UnsupportedOperationException("Unsupported platform!");
		}

		if (plugin != null) {
			plugin.onServerStarted(); //Note: ServerStartedEvent is not called as that would go to all plugins. Here is slightly different behavior between the two
		}
	}

	public void unloadPlugin(OsmiumPlugin plugin) {
		OsmiumLogger.info("Unloading plugin: " + plugin.getName());

		if (Platform.isBukkit()) {
			BukkitAccess.unloadPlugin(plugin);
		} else if (Platform.isSponge()) {
			SpongeAccess.unloadPlugin(plugin);
		} else if (Platform.isBungeeCord()) {
			BungeeAccess.unloadPlugin(plugin);
		}

		Osmium.getEventManager().unregister(plugin);
		Osmium.getConfigManager().unregister(plugin);
		Osmium.getCommandManager().unregister(plugin);
		//TODO: Serialization class may remember stuff (minor memory leak only, shouldn't interfere with restart)

		plugins.remove(plugin.getClass());
		pluginFiles.remove(Directory.getJarFilePath(plugin.getClass()));
		externalClasses.entrySet().removeIf(e -> e.getValue() == plugin);
	}

	public void assignPluginToExternalClass(Class<?> cls, OsmiumPlugin plugin) {
		externalClasses.put(cls, plugin);
	}

	public Collection<OsmiumPlugin> getPlugins() {
		return plugins.values();
	}

	public Optional<OsmiumPlugin> getPlugin(String name) {
		for (OsmiumPlugin plugin : plugins.values()) {
			if (plugin.getName().equalsIgnoreCase(name)) {
				return Optional.of(plugin);
			}
		}
		return Optional.empty();
	}

	public OsmiumPlugin getPlugin(Class<?> cls) {
		OsmiumPlugin plugin = pluginFiles.get(Directory.getJarFilePath(cls));
		if (plugin != null) {
			return plugin;
		}
		return externalClasses.get(cls);
	}

	public Path getPluginJarFile(String pluginName) {
		return pluginPaths.get(pluginName);
	}

}
