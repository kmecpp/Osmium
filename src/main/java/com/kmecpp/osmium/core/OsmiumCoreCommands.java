package com.kmecpp.osmium.core;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.StringUtil;

// @CommandProperties(aliases = { "osmium", "os", "om", "o" }, description =
// "Base command for interacting with the Osmium API")
public class OsmiumCoreCommands extends Command {

	public OsmiumCoreCommands() {
		super("osmium", "os", "om", "o");
		setDescription("Base command for interacting with the Osmium API");

		add("info").setDescription("Displays information about the Osmium API").setExecutor((e) -> {
			e.send("&aPlugin: " + "&b" + AppInfo.NAME);
			e.send("&aVersion: " + "&b" + AppInfo.VERSION);
			e.send("&aAuthor: " + "&b" + "kmecpp");
			e.send("&aWebsite: " + "&b" + "https://github.com/kmecpp/Osmium");
		});

		add("tps").setDescription("Displays the server TPS").setExecutor((e) -> {
			e.send("&aLast 60s: &b" + StringUtil.round(TPSTask.getAverage(60), 1));
			e.send("&aLast 30s: &b" + StringUtil.round(TPSTask.getAverage(30), 1));
			e.send("&aLast 10s: &b" + StringUtil.round(TPSTask.getAverage(10), 1));
			e.send("&aLast Tick: &b" + StringUtil.round(TPSTask.getLastTickSpeed(), 1));
		});

		add("debug").setDescription("Toggles debug mode").setExecutor((e) -> {
			boolean result = OsmiumCoreConfig.debug = !OsmiumCoreConfig.debug;
			Osmium.saveConfig(OsmiumCoreConfig.class);
			e.send("&eOsmium debug mode: " + (result ? "&aenabled" : "&cdisabled"));
		});

		add("reload").setAdmin(true).setExecutor((e) -> {
			int count = 0;
			for (OsmiumPlugin plugin : Osmium.getPlugins()) {
				plugin.reload();
				count++;
			}
			e.send("&a" + count + " Osmium plugins reloaded successfully!");
		});

		add("plugins").setAdmin(true).setUsage("{plugin}").setExecutor((e) -> {
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

		add("commands").setAdmin(true).setUsage("<plugin>").setExecutor((e) -> {
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

		add("user").setAdmin(true).setUsage("<player>").setExecutor(e -> {
			String name = e.getString(0);
			Optional<UUID> optional = Osmium.getUUID(name);
			if (!optional.isPresent()) {
				e.sendMessage(Chat.RED + "[Warning] That player has never joined the server");
				optional = Osmium.lookupUUID(name);
				if (!optional.isPresent()) {
					fail("That player does not exist!");
				}
			}

			Optional<Integer> id = Osmium.getUserId(optional.get());
			if (id.isPresent()) {
				e.sendMessage(Chat.GREEN + "User ID for " + name + ": " + Chat.AQUA + id.get());
			} else {
				fail("That player does not have a user ID associated with their account");
			}
		});

		add("clearcooldowns", "cc").setAdmin(true).setUsage("<player>").setExecutor(e -> {
			User user = e.getUser(0);
			CommandManager.getCooldownData().remove(user.getUniqueId());
			e.sendMessage(Chat.GREEN + "User cooldowns cleared successfully!");
		});
	}

}
