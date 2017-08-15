package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.OsmiumData;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class BukkitPlugin extends JavaPlugin {

	private static final OsmiumPlugin PLUGIN = OsmiumData.constructPlugin();

	private static BukkitPlugin instance;

	public BukkitPlugin() {
		if (instance != null) {
			throw new RuntimeException("Plugin already constructed!");
		}
		instance = this;
	}

	public static BukkitPlugin getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		PLUGIN.onLoad();
	}

	@Override
	public void onEnable() {
		PLUGIN.preInit();
		OsmiumPlugin.getInitializer().preInit();

		PLUGIN.init();
		OsmiumPlugin.getInitializer().init();

		PLUGIN.postInit();
		OsmiumPlugin.getInitializer().postInit();
	}

}
