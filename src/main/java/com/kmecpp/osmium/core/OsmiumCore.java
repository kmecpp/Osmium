package com.kmecpp.osmium.core;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.inventory.InventoryMenu;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.api.tasks.TimeUnit;

@Plugin(name = AppInfo.NAME, version = AppInfo.VERSION, authors = { AppInfo.AUTHOR }, url = AppInfo.URL)
public class OsmiumCore extends OsmiumPlugin {

	private static OsmiumCore instance;

	public OsmiumCore() {
		if (instance == null) {
			instance = this;
		} else {
			throw new IllegalStateException("Plugin already constructed!");
		}
	}

	@Override
	public void onInit() {
		enableMetrics();

		Osmium.getTask().setAsync(true).setTime(1, 1, TimeUnit.HOUR).setExecutor(task -> {
			saveAllData();
		});

		this.getClassProcessor().onEnable(InventoryMenu.class);

		//		if (Platform.isBukkit()) {
		//			this.metricsImplementation = new OsmiumMetrics(getPluginImplementation());
		//		} else if (Platform.isSponge()) {
		//			PluginContainer plugin = getPluginImplementation();
		//			this.metricsImplementation = Reflection.newInstance(SpongeMetrics.class, plugin, plugin.getLogger(), Sponge.getConfigManager().getSharedConfig(plugin).getDirectory());
		//			//			Reflection.newInstance(org.bstats.sponge.Metrics.class);
		//			//			this.metricsImplementation = org.bstats.sponge.Metrics.
		//		}
	}

	@Override
	public void onDisable() {
		saveAllData();
	}

	private static void saveAllData() {
		for (OsmiumPlugin plugin : Osmium.getPlugins()) {
			try {
				Osmium.savePluginData(plugin);
				Osmium.getPlayerDataManager().saveAllPlayers();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@Override
	public void onPostInit() {
		Osmium.getConfigManager().lateInit();
	}

	public static OsmiumCore getPlugin() {
		return instance;
	}

}
