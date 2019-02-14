package com.kmecpp.osmium.core;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.inventory.InventoryMenu;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;
import com.kmecpp.osmium.api.tasks.Schedule;
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

	@Schedule(async = true, delay = 1, interval = 1, unit = TimeUnit.HOUR)
	public void periodicSave() {
		for (OsmiumPlugin plugin : Osmium.getPlugins()) {
			Osmium.savePluginData(plugin);
		}
	}

	@Override
	public void onInit() {
		enableMetrics();

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

	public static OsmiumCore getPlugin() {
		return instance;
	}

}
