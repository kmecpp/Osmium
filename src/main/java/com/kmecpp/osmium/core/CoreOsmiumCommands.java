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
					e.sendStyledMessage("&aPlugin: " + "&b" + AppInfo.NAME);
					e.sendStyledMessage("&aVersion: " + "&b" + AppInfo.VERSION);
					e.sendStyledMessage("&aAuthor: " + "&b" + "kmecpp");
					e.sendStyledMessage("&aWebsite: " + "&b" + "https://github.com/kmecpp/Osmium");
				});

		add("reload")
				.setAdmin()
				.setExecutor((e) -> {
					for (OsmiumPlugin plugin : Osmium.getPlugins()) {
						plugin.onReload();
					}
					e.sendStyledMessage("&aOsmium plugins reloaded successfully!");
				});

		add("plugins")
				.setAdmin()
				.setUsage("{plugin}")
				.setExecutor((e) -> {
					if (e.isBaseCommand()) {
						e.sendTitle("Osmium Plugins");
						for (OsmiumPlugin plugin : Osmium.getPlugins()) {
							e.sendStyledMessage("&e - &a" + plugin.getName() + " &ev&b" + plugin.getVersion());
						}
					} else if (e.getArgs().length == 1) {
						OsmiumPlugin plugin = e.getPlugin(0);
						e.sendTitle("Osmium Plugin Info");
						e.sendStyledMessage("&aName: &b" + plugin.getName());
						e.sendStyledMessage("&aVersion: " + "&b" + plugin.getVersion());
						if (plugin.hasAuthors()) {
							e.sendStyledMessage("&aAuthor" + (plugin.getAuthors().length >= 2 ? "s" : "") + ": " + "&b" + String.join(", ", plugin.getAuthors()));
						}
						if (plugin.hasWebsite()) {
							e.sendStyledMessage("&aWebsite: " + "&b" + plugin.getWebsite());
						}
						if (plugin.hasDependencies()) {
							e.sendStyledMessage("&aDependencies: " + "&b" + String.join(", ", plugin.getDependencies()));
						}
					} else {
						usageError();
					}
				});

		add("commands")
				.setAdmin()
				.setUsage("<plugin>")
				.setExecutor((e) -> {
					OsmiumPlugin plugin = e.getPlugin(0);
					e.sendTitle(plugin.getName() + " Commands");
					for (Command command : Osmium.getCommandManager().getCommands(plugin)) {
						e.sendStyledMessage("&b/" + command.getPrimaryAlias() + (command.hasDescription() ? "&e - &a" + command.getDescription() : ""));
						e.sendStyledMessage("    &bAliases: " + Arrays.toString(command.getAliases()));
						if (command.hasPermission()) {
							e.sendStyledMessage("    &bPermission: " + command.getPermission());
						}
					}
				});
	}

}
