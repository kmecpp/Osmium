package com.kmecpp.osmium.core;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.inventory.menu.InventoryManager;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.api.tasks.TimeUnit;
import com.kmecpp.osmium.api.util.TimeUtil;

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
	public void onLoad() {
		TimeUtil.setTimeZone(CoreOsmiumConfig.timeZone);
	}

	@Override
	public void onInit() {
		enableMetrics();

		Osmium.getPlayerDataManager().start();

		Osmium.getTask().setTime(0, 1, TimeUnit.MINUTE).setExecutor((t) -> {
			OsmiumData.update();
		}).start();

		Osmium.getTask().setAsync(true).setTime(0, 1, TimeUnit.HOUR).setExecutor(task -> {
			saveAllData();
		}).start();

		this.getClassProcessor().onEnable(InventoryManager.class);

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
	public void onPostInit() {
		Osmium.getConfigManager().lateInit();
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

	public static OsmiumCore getPlugin() {
		return instance;
	}

}
