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
					e.sendMessage("&aPlugin: " + "&b" + AppInfo.NAME);
					e.sendMessage("&aVersion: " + "&b" + AppInfo.VERSION);
					e.sendMessage("&aAuthor: " + "&b" + "kmecpp");
					e.sendMessage("&aWebsite: " + "&b" + "https://github.com/kmecpp/Osmium");
				});

		add("reload")
				.setAdmin()
				.setExecutor((e) -> {
					//			org.bukkit.event.server.pl
					//			org.spongepowered.common.event.tracking.phase.plugin.
					e.sendMessage("&aOsmium plugins reloaded successfully!");
				});

		add("plugins")
				.setAdmin()
				.setUsage("{plugin}")
				.setExecutor((e) -> {
					if (e.isEmpty()) {
						e.sendTitle("Osmium Plugins");
						for (OsmiumPlugin plugin : Osmium.getPlugins()) {
							e.sendMessage("&e - &a" + plugin.getName() + " &ev&b" + plugin.getVersion());
						}
					} else if (e.getArgs().length == 1) {
						OsmiumPlugin plugin = e.getPlugin(0);
						e.sendTitle("Osmium Plugin Info");
						e.sendMessage("&aName: &b" + plugin.getName());
						e.sendMessage("&aVersion: " + "&b" + plugin.getVersion());
						if (plugin.hasAuthors()) {
							e.sendMessage("&aAuthor" + (plugin.getAuthors().length >= 2 ? "s" : "") + ": " + "&b" + String.join(", ", plugin.getAuthors()));
						}
						if (plugin.hasWebsite()) {
							e.sendMessage("&aWebsite: " + "&b" + plugin.getWebsite());
						}
						if (plugin.hasDependencies()) {
							e.sendMessage("&bDependencies: " + "&b" + String.join(", ", plugin.getDependencies()));
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
						e.sendMessage("&b/" + command.getPrimaryAlias() + (command.hasDescription() ? "&e - &a" + command.getDescription() : ""));
						e.sendMessage("    &bAliases: " + Arrays.toString(command.getAliases()));
						if (command.hasPermission()) {
							e.sendMessage("    &bPermission: " + command.getPermission());
						}
					}
				});
	}

}
