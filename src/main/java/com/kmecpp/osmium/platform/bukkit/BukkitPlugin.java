package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class BukkitPlugin extends JavaPlugin {

	private static final OsmiumPlugin plugin = OsmiumPlugin.getPlugin();

	public static OsmiumPlugin getPlugin() {
		return plugin;
	}

}
