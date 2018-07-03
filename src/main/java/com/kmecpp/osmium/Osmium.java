package com.kmecpp.osmium;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

import com.kmecpp.jlib.reflection.Reflection;
import com.kmecpp.jlib.utils.IOUtil;
import com.kmecpp.osmium.api.config.ConfigManager;
import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.tasks.Scheduler;

public final class Osmium {

	public static final String OSMIUM = "Osmium";

	private static final HashMap<Class<? extends OsmiumPlugin>, OsmiumPlugin> plugins = new HashMap<>();

	private static final ConfigManager configManager = new ConfigManager();
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

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

	public static void reloadConfig(Class<?> cls) {
		configManager.loadConfig(cls);
	}

	public static void saveConfig(Class<?> cls) {
		configManager.saveConfig(cls);
	}

	public static void on(Platform platform, Runnable runnable) {
		if (Platform.getPlatform() == platform) {
			runnable.run();
		}
	}

	public static <T> T getValue(Callable<T> bukkit, Callable<T> sponge) {
		try {
			return Platform.isBukkit() ? bukkit.call() : Platform.isSponge() ? sponge.call() : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static OsmiumPlugin loadPlugin(Object pluginImpl) {
		try {
			String[] lines = IOUtil.readLines(pluginImpl.getClass().getResource("/osmium.properties"));
			try {
				String mainClassName = lines[0].split(":")[1].trim();
				ClassLoader pluginClassLoader = pluginImpl.getClass().getClassLoader();
				Class<? extends OsmiumPlugin> main = pluginClassLoader.loadClass(mainClassName).asSubclass(OsmiumPlugin.class);

				OsmiumPlugin plugin = main.newInstance();
				Reflection.invokeMethod(OsmiumPlugin.class, plugin, "setupPlugin", pluginImpl);
				plugins.put(main, plugin);
				return plugin;
			} catch (Exception e) {
				throw new RuntimeException("Could not load Osmium plugin: " + lines[1].split(":")[1].trim(), e);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read osmium.properties from jar: " + pluginImpl.getClass().getName(), e);
		}

	}

}
