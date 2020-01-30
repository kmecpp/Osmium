package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.osmium.PluginReloadEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class OsmiumPluginReloadEvent implements PluginReloadEvent {

	private OsmiumPlugin plugin;
	private boolean fullReload;

	public OsmiumPluginReloadEvent(OsmiumPlugin plugin, boolean fullReload) {
		this.plugin = plugin;
		this.fullReload = fullReload;
	}

	@Override
	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	@Override
	public boolean isFullReload() {
		return fullReload;
	}

}
