package com.kmecpp.osmium.core;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;

@Plugin(name = AppInfo.NAME, version = AppInfo.VERSION, authors = { AppInfo.AUTHOR }, url = AppInfo.URL)
public class CoreOsmiumPlugin extends OsmiumPlugin {

	private static CoreOsmiumPlugin instance;

	public CoreOsmiumPlugin() {
		if (instance == null) {
			instance = this;
		} else {
			throw new IllegalStateException("Plugin already constructed!");
		}
	}

	@Override
	public void onInit() {
		enableMetrics();
		//		if (Platform.isBukkit()) {
		//			this.metricsImplementation = new OsmiumMetrics(getPluginImplementation());
		//		} else if (Platform.isSponge()) {
		//			PluginContainer plugin = getPluginImplementation();
		//			this.metricsImplementation = Reflection.newInstance(SpongeMetrics.class, plugin, plugin.getLogger(), Sponge.getConfigManager().getSharedConfig(plugin).getDirectory());
		//			//			Reflection.newInstance(org.bstats.sponge.Metrics.class);
		//			//			this.metricsImplementation = org.bstats.sponge.Metrics.
		//		}
	}

	public static CoreOsmiumPlugin getInstance() {
		return instance;
	}

}
