package com.kmecpp.osmium.api.plugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.kmecpp.osmium.Directory;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.config.PluginConfigTypeData;
import com.kmecpp.osmium.api.event.events.osmium.PluginRefreshEvent;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.IOUtil;
import com.kmecpp.osmium.platform.osmium.OsmiumPluginRefreshEvent;

public class PluginLoader {

	private final HashMap<String, OsmiumPlugin> pluginFiles = new HashMap<>();
	private final HashMap<Class<? extends OsmiumPlugin>, OsmiumPlugin> plugins = new HashMap<>();

	private final HashMap<Class<?>, OsmiumPlugin> externalClasses = new HashMap<>();

	public OsmiumPlugin createOsmiumPlugin(Object pluginImpl) {
		try {
			//			String[] lines = IOUtil.readLines(pluginImpl.getClass().getResource("/osmium.properties")); //Weird sponge bug. Doesn't work anymore
			JarFile jar = Directory.getJarFile(pluginImpl.getClass());
			String[] lines = IOUtil.readLines(jar.getInputStream(jar.getEntry("osmium.properties")));
			try {
				String mainClassName = lines[0].split(":")[1].trim();
				ClassLoader pluginClassLoader = pluginImpl.getClass().getClassLoader();
				Class<? extends OsmiumPlugin> main;
				try {
					main = pluginClassLoader.loadClass(mainClassName).asSubclass(OsmiumPlugin.class);
				} catch (ClassCastException e) {
					OsmiumLogger.error("Failed to load plugin: " + mainClassName + ". Does this class extend " + OsmiumPlugin.class.getSimpleName() + "?");
					throw new RuntimeException(e);
				}

				OsmiumPlugin plugin = main.newInstance();
				OsmiumLogger.info("Loading plugin: " + plugin.getName() + " v" + plugin.getVersion());

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
				PluginConfigTypeData configTypeData = PluginConfigTypeData.parse(configTypes);

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

}
