package com.kmecpp.osmium;

import java.util.HashMap;

import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.jlib.utils.IOUtil;
import com.kmecpp.osmium.api.event.EventManager;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.tasks.Scheduler;

public final class Osmium {

	public static final String OSMIUM = "Osmium";

	private static final HashMap<Class<? extends OsmiumPlugin>, OsmiumPlugin> plugins = new HashMap<>();

	private static final EventManager eventManager = new EventManager();
	private static final Scheduler scheduler = new Scheduler();

	/*
	 * TODO:
	 * - Configs
	 * - Commands
	 * - Schedulers
	 * 
	 * - More events
	 * 
	 */

	private Osmium() {
	}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

	public static void reloadConfig(Class<?> cls) {
		
	}

	public static void saveConfig(Class<?> cls) {

	}

	public static OsmiumPlugin loadPlugin(Object pluginImpl) {
		try {
			//			Bukkit.getPluginManager().loadPlugin(new File(pluginImpl.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
			String[] lines = IOUtil.readLines(pluginImpl.getClass().getResource("/osmium.properties"));
			String mainClassName = lines[0].split(":")[1].trim();
			ClassLoader pluginClassLoader = pluginImpl.getClass().getClassLoader();

			Class<? extends OsmiumPlugin> main = pluginClassLoader.loadClass(mainClassName).asSubclass(OsmiumPlugin.class);
			//			Class<? extends OsmiumPlugin> main = URLClassLoader
			//					.newInstance(new URL[] { pluginImpl.getClass().getProtectionDomain().getCodeSource().getLocation() }, Osmium.class.getClassLoader())
			//					.loadClass(mainClassName)
			//					.asSubclass(OsmiumPlugin.class);
			//			Class<? extends OsmiumPlugin> main = Class.forName(mainClassName).asSubclass(OsmiumPlugin.class);

			OsmiumPlugin plugin = main.newInstance();
			Reflection.invokeMethod(OsmiumPlugin.class, plugin, "setupPlugin", pluginImpl);
			//			Reflection.setField(OsmiumPlugin.class, plugin, "pluginImpl", pluginImpl);
			plugins.put(main, plugin);

			//			JarFile jar = new JarFile(new File(pluginImpl.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
			//			Reflection.setField(OsmiumPlugin.class,
			//					plugin,
			//					"pluginClasses",
			//					Reflection.getClasses(pluginClassLoader, jar, ""));

			return plugin;
		} catch (Exception e) {
			try {
				String name = IOUtil.readLines(pluginImpl.getClass().getResource("/osmium.properties"))[1].split(":")[1].trim();
				throw new RuntimeException("Could not load Osmium plugin: " + name, e);
			} catch (Exception ex) {
				throw new RuntimeException("Invalid Osmium plugin: " + pluginImpl.getClass().getName(), e);
			}
		}

	}

}
