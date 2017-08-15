package com.kmecpp.osmiumplugin;

import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.plugin.Plugin;

@Plugin(name = "Example Plugin", version = "1.0")
public class ExamplePlugin extends OsmiumPlugin {

	@Override
	public void preInit() {
		CommandManager.register("operators", "ops")
				.description("Gets a list of operators")
				.permission("exampleplugin.ops")
				.executor((sender, label, args) -> {
					sender.sendMessage("You just executed a command!");
				});
	}

}
