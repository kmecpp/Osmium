package com.kmecpp.osmium.core;

import java.util.Arrays;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandProperties;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

@CommandProperties(aliases = { "osmium", "os", "om", "o" }, description = "Base command for interacting with the Osmium API")
public class CoreOsmiumCommands extends Command {

	@Override
	public void configure() {
		add("info")
				.setDescription("Displays information about the Osmium API")
				.setExecutor((e) -> {
					e.send("&aPlugin: " + "&b" + AppInfo.NAME);
					e.send("&aVersion: " + "&b" + AppInfo.VERSION);
					e.send("&aAuthor: " + "&b" + "kmecpp");
					e.send("&aWebsite: " + "&b" + "https://github.com/kmecpp/Osmium");
				});

		add("reload")
				.setAdmin(true)
				.setExecutor((e) -> {
					for (OsmiumPlugin plugin : Osmium.getPlugins()) {
						plugin.onReload();
					}
					e.send("&aOsmium plugins reloaded successfully!");
				});

		add("plugins")
				.setAdmin(true)
				.setUsage("{plugin}")
				.setExecutor((e) -> {
					if (e.isBaseCommand()) {
						e.sendTitle("Osmium Plugins");
						for (OsmiumPlugin plugin : Osmium.getPlugins()) {
							e.send("&e - &a" + plugin.getName() + " &ev&b" + plugin.getVersion());
						}
					} else if (e.getArgs().length == 1) {
						OsmiumPlugin plugin = e.getPlugin(0);
						e.sendTitle("Osmium Plugin Info");
						e.send("&aName: &b" + plugin.getName());
						e.send("&aVersion: " + "&b" + plugin.getVersion());
						if (plugin.hasAuthors()) {
							e.send("&aAuthor" + (plugin.getAuthors().length >= 2 ? "s" : "") + ": " + "&b" + String.join(", ", plugin.getAuthors()));
						}
						if (plugin.hasWebsite()) {
							e.send("&aWebsite: " + "&b" + plugin.getWebsite());
						}
						if (plugin.hasDependencies()) {
							e.send("&aDependencies: " + "&b" + String.join(", ", plugin.getDependencies()));
						}
					} else {
						usageError();
					}
				});

		add("commands")
				.setAdmin(true)
				.setUsage("<plugin>")
				.setExecutor((e) -> {
					OsmiumPlugin plugin = e.getPlugin(0);
					e.sendTitle(plugin.getName() + " Commands");
					for (Command command : Osmium.getCommandManager().getCommands(plugin)) {
						e.send("&b/" + command.getPrimaryAlias() + (command.hasDescription() ? "&e - &a" + command.getDescription() : ""));
						e.send("    &bAliases: " + Arrays.toString(command.getAliases()));
						if (command.hasPermission()) {
							e.send("    &bPermission: " + command.getPermission());
						}
					}
				});
	}

}
