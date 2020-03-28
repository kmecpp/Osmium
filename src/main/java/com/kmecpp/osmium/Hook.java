package com.kmecpp.osmium;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;
import com.kmecpp.osmium.api.util.StringUtil;

public class Hook<T> {

	private T hook;
	private Callable<T> loader;
	private boolean loaded;

	private Hook(T hook) {
		this.hook = hook;
		this.loaded = true;
	}

	private Hook(Callable<T> loader) {
		this.loader = loader;
	}

	public static <T> Hook<T> of(T inst) {
		return new Hook<T>(inst);
	}

	public static <T> Hook<T> empty() {
		return new Hook<>(null);
	}

	public void ensureLoaded() {
		if (!loaded) {
			try {
				this.hook = loader.call();
				this.loader = null;
			} catch (Throwable t) {
				t.printStackTrace();
			}
			loaded = true;
		}
	}

	public boolean isLoaded() {
		ensureLoaded();
		return hook != null;
	}

	public T get() {
		ensureLoaded();
		return hook;
	}

	public static <T> Hook<T> get(Callable<T> loader) {
		return new Hook<>(loader);
	}

	@SuppressWarnings("unchecked")
	public static <T> Hook<T> get(String accessor) {
		OsmiumPlugin callingPlugin = Osmium.getPlugin(Reflection.getInvokingClass(1));
		if (!StringUtil.contains(accessor, callingPlugin.getDependencies())) {
			OsmiumLogger.warn(callingPlugin.getName() + " is attempting to hook into " + accessor + " but it is not specified as a dependency");
		}

		Hook<T> pluginHook = (Hook<T>) new Hook<>(() -> {
			if (Platform.isBukkit()) {
				return Bukkit.getPluginManager().getPlugin(accessor);
			} else if (Platform.isSponge()) {
				for (PluginContainer pluginContainer : Sponge.getPluginManager().getPlugins()) {
					if (pluginContainer.getName().equalsIgnoreCase(accessor) || pluginContainer.getId().equalsIgnoreCase(accessor)) {
						return pluginContainer.getInstance().orElse(null);
					}
				}
			}
			return null;
		});

		if (pluginHook.isLoaded()) {
			return pluginHook;
		}

		return (Hook<T>) new Hook<>(() -> {
			String[] parts = accessor.split("\\:\\:");
			if (parts.length != 2) {
				throw new IllegalArgumentException("Unable to load dependency: " + accessor);
			}
			String className = parts[0];
			String methodName = parts[1];

			try {
				return Class.forName(className).getMethod(methodName).invoke(null);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	//	@SuppressWarnings("unchecked")
	//	public static <T> T getFrom(String method) {
	//
	//	}

}
