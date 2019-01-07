package com.kmecpp.osmium;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import com.kmecpp.osmium.api.platform.Platform;

public class Hook<T> {

	private T plugin;

	public Hook(T plugin) {
		this.plugin = plugin;
	}

	public boolean isLoaded() {
		return plugin != null;
	}

	public T getPlugin() {
		return plugin;
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

}
