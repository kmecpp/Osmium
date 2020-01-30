package com.kmecpp.osmium.platform.osmium;

import com.kmecpp.osmium.api.event.events.osmium.PluginConfigureEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class OsmiumPluginConfigureEvent implements PluginConfigureEvent {

	private OsmiumPlugin plugin;
	private boolean full;

	public OsmiumPluginConfigureEvent(OsmiumPlugin plugin, boolean full) {
		this.plugin = plugin;
		this.full = full;
	}

	@Override
	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	@Override
	public boolean isFull() {
		return full;
	}

}
