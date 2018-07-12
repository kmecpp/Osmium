package com.kmecpp.osmium.core;

import com.kmecpp.osmium.api.command.OsmiumCommand;

public class CoreOsmiumCommands extends OsmiumCommand {

	@Override
	public void configure() {
		add("info");
		add("reload");
		add("plugins");
	}

}
