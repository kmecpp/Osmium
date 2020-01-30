package com.kmecpp.osmium.api.event.events.osmium;

import com.kmecpp.osmium.api.event.Event;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public interface PluginConfigureEvent extends Event {

	OsmiumPlugin getPlugin();

	boolean isFull();

}
