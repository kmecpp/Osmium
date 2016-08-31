package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

	private static BukkitPlugin plugin;

	public BukkitPlugin() {
		plugin = this;
	}

	public static BukkitPlugin getPlugin() {
		return plugin;
	}

}
