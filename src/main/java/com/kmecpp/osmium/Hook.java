package com.kmecpp.osmium;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import com.kmecpp.osmium.api.platform.Platform;

public class Hook<T> {

	private T hook;

	private Hook(T hook) {
		this.hook = hook;
	}

	public boolean isLoaded() {
		return hook != null;
	}

	public T get() {
		return hook;
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String pluginName) {
		Object result = null;
		if (Platform.isBukkit()) {
			result = Bukkit.getPluginManager().getPlugin(pluginName);
		} else if (Platform.isSponge()) {
			for (PluginContainer pluginContainer : Sponge.getPluginManager().getPlugins()) {
				if (pluginContainer.getName().equalsIgnoreCase(pluginName) || pluginContainer.getId().equalsIgnoreCase(pluginName)) {
					result = pluginContainer.getInstance().orElse(null);
				}
			}
		}
		return (T) new Hook<T>((T) result);
	}

	public static <T> Hook<T> get(Callable<T> callable) {
		try {
			return new Hook<T>(callable.call());
		} catch (Exception e) {
			e.printStackTrace();
			return new Hook<T>(null);
		}
	}

}
