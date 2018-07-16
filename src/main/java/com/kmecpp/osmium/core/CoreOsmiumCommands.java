package com.kmecpp.osmium.core;

import java.util.Optional;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.OsmiumCommand;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

@Command(aliases = { "osmium", "os", "om", "o" }, description = "Base command for interacting with the Osmium API")
public class CoreOsmiumCommands extends OsmiumCommand {

	@Override
	public void configure() {
		add("info")
				.setExecutor((e) -> {
					e.sendMessage("&aPlugin: " + "&b" + Osmium.NAME);
					e.sendMessage("&aVersion: " + "&b" + Osmium.VERSION);
					e.sendMessage("&aAuthor: " + "&b" + "kmecpp");
					e.sendMessage("&aURL: " + "&b" + "https://github.com/kmecpp/Osmium");
				});

		add("reload")
				.setAdmin()
				.setExecutor((e) -> {
					//			org.bukkit.event.server.pl
					//			org.spongepowered.common.event.tracking.phase.plugin.
				});

		add("plugins")
				.setAdmin()
				.setUsage("plugins {plugin}")
				.setExecutor((e) -> {
					if (e.isEmpty()) {
						e.sendTitle("Osmium Plugins");
						for (OsmiumPlugin plugin : Osmium.getPlugins()) {
							e.sendMessage("&e - &a" + plugin.getName() + " &ev&b" + plugin.getVersion());
						}
					} else if (e.getArgs().length == 1) {
						Optional<OsmiumPlugin> optionalPlugin = Osmium.getPlugin(e.getArg(0));
						if (optionalPlugin.isPresent()) {
							OsmiumPlugin plugin = optionalPlugin.get();
							e.sendTitle("Osmium Plugin Info");
							e.sendMessage("&aName: &b" + plugin.getName());
							e.sendMessage("&aVersion: " + "&b" + plugin.getVersion());
							e.sendMessage("&aAuthor: " + "&b" + String.join(", ", plugin.getAuthors()));
							e.sendMessage("&aURL: " + "&b" + plugin.getUrl());
							e.sendMessage("&bDependencies: " + "&b" + String.join(", ", plugin.getDependencies()));
						} else {
							notFoundError("plugin", e.getArg(0));
						}
					} else {
						usageError();
					}
				});
	}

}
