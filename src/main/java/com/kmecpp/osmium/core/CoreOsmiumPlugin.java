package com.kmecpp.osmium.core;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;

@Plugin(name = Osmium.NAME, version = Osmium.VERSION, authors = { "kmecpp" }, url = "https://github.com/kmecpp/Osmium")
public class CoreOsmiumPlugin extends OsmiumPlugin {

	@Override
	public void init() {
		Osmium.registerCommand("osmium", "os", "o");
	}

}
