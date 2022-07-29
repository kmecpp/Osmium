package com.kmecpp.osmium.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.GameProfile;
import com.kmecpp.osmium.api.User;
import com.kmecpp.osmium.api.command.Chat;
import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.StringUtil;

// @CommandProperties(aliases = { "osmium", "os", "om", "o" }, description =
// "Base command for interacting with the Osmium API")
public class OsmiumCoreCommands extends Command {

	private static final HashSet<UUID> playersWaitingForAliases = new HashSet<>();
	private static final UUID CONSOLE_UUID = new UUID(0, 0);

	public OsmiumCoreCommands() {
		super(Platform.isGame() ? "osmium" : "osmiumbungee", Platform.isGame() ? new String[] { "os", "o" } : new String[] { "osb", "ob" });
		if (Platform.isProxy()) {
			setTitle("Osmium BungeeCord Commands");
		}

		setDescription("Base command for interacting with the Osmium API");
		addHelpCommand();

		add("info").setDescription("Displays information about the Osmium API").setExecutor(e -> {
			e.send("&aPlugin: " + "&b" + AppInfo.NAME);
			e.send("&aVersion: " + "&b" + AppInfo.VERSION);
			e.send("&aAuthor: " + "&b" + "kmecpp");
			e.send("&aWebsite: " + "&b" + "https://github.com/kmecpp/Osmium");
		});

		add("aliases").setUsage("<command>").setDescription("View the aliases for the given Osmium command").setExecutor(e -> {
			String command = e.getRemainingJoined(0);
			if (command.isEmpty()) {
				usageError();
			}
			UUID senderId = e.isPlayer() ? e.getPlayer().getUniqueId() : CONSOLE_UUID;
			playersWaitingForAliases.add(senderId);

			Osmium.getCommandManager().processCommand(e.getSender(), command);
		});

		add("tps").setDescription("Displays the server TPS").setPermission("osmium.command.tps").setExecutor(e -> {
			e.send("&aLast 60s: &b" + StringUtil.round(TPSTask.getAverage(60), 1));
			e.send("&aLast 30s: &b" + StringUtil.round(TPSTask.getAverage(30), 1));
			e.send("&aLast 10s: &b" + StringUtil.round(TPSTask.getAverage(10), 1));
			e.send("&aLast Tick: &b" + StringUtil.round(TPSTask.getLastTickSpeed(), 1));
		});

		add("debug").setDescription("Toggles debug mode").setPermission("osmium.command.debug").setExecutor(e -> {
			boolean result = OsmiumCoreConfig.debug = !OsmiumCoreConfig.debug;
			Osmium.saveConfig(OsmiumCoreConfig.class);
			e.send("&eOsmium debug mode: " + (result ? "&aenabled" : "&cdisabled"));
		});

		add("reload").setUsage("{<plugin>/all} [full]").setAdmin(true).setExecutor(e -> {
			long startTime = System.currentTimeMillis();

			String pluginName = e.getString(0);
			boolean full = e.getString(1, "").equalsIgnoreCase("full");

			if (pluginName.equalsIgnoreCase("full")) {
				Osmium.reloadPlugin(OsmiumCore.getPlugin(), true);
				e.sendMessage(Chat.GREEN + OsmiumCore.getPlugin().getName() + " reloaded successfully (" + (System.currentTimeMillis() - startTime) + "ms)!");
			} else if (pluginName.equalsIgnoreCase("all")) {
				int count = 0;
				for (OsmiumPlugin plugin : Osmium.getPlugins()) {
					Osmium.reloadPlugin(plugin, full);
					count++;
				}
				e.sendMessage(Chat.GREEN + "" + count + " Osmium plugins reloaded successfully (" + (System.currentTimeMillis() - startTime) + "ms)!");
			} else {
				OsmiumPlugin plugin = e.getPlugin(0);
				Osmium.reloadPlugin(plugin, full);
				e.sendMessage(Chat.GREEN + plugin.getName() + " reloaded successfully (" + (System.currentTimeMillis() - startTime) + "ms)!");
			}
		});

		add("<load/unload/restart>").setAdmin(true).setUsage("<plugin>").setExecutor(e -> {
			long startTime = System.currentTimeMillis();
			OsmiumPlugin plugin = e.getPlugin(0, null);

			if (e.getArgLabel().equalsIgnoreCase("load")) {
				if (plugin != null) {
					fail("That plugin is already loaded!");
				}

				String nameOrPath = e.getString(0);
				Path jarFile = Osmium.getPluginLoader().getPluginJarFile(nameOrPath);
				if (jarFile == null) {
					jarFile = Paths.get("plugins", nameOrPath);
				}

				if (Files.exists(jarFile)) {
					fail("That plugin does not exist! Enter a plugin or file name!");
				}

				Osmium.getPluginLoader().loadPlugin(jarFile.toFile());
				e.sendMessage(Chat.GREEN + "Plugin loaded successfully (" + (System.currentTimeMillis() - startTime) + "ms)!");
			} else {
				if (plugin == null) {
					fail("That plugin is not currently loaded!");
				}

				if (e.getArgLabel().equalsIgnoreCase("unload")) {
					Osmium.getPluginLoader().unloadPlugin(plugin);
					e.sendMessage(Chat.GREEN + plugin.getName() + " unloaded successfully (" + (System.currentTimeMillis() - startTime) + "ms)!");
				} else if (e.getArgLabel().equalsIgnoreCase("restart")) {
					Osmium.getPluginLoader().restartPlugin(plugin);
					e.sendMessage(Chat.GREEN + plugin.getName() + " reloaded successfully (" + (System.currentTimeMillis() - startTime) + "ms)!");
				}
			}
		});

		add("plugins").setAdmin(true).setUsage("{plugin}").setExecutor(e -> {
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

		add("commands").setAdmin(true).setUsage("<plugin>").setExecutor(e -> {
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

		add("whois").setAdmin(true).setUsage("<user-id>").setExecutor(e -> {
			int userId = e.getInt(0);
			Optional<GameProfile> optional = Osmium.getGameProfile(userId);
			if (optional.isPresent()) {
				e.sendMessage(Chat.GREEN + "User " + userId + " is " + optional.get().getName() + " (" + optional.get().getUniqueId() + ")");
			} else {
				fail("Unknown user: " + Chat.YELLOW + userId);
			}
		});

		add("clearcooldowns", "cc").setAdmin(true).setUsage("<player>").setExecutor(e -> {
			User user = e.getUser(0);
			Osmium.getCommandManager().getCooldownData().remove(user.getUniqueId());
			e.sendMessage(Chat.GREEN + "User cooldowns cleared successfully!");
		});
	}

	public static boolean processAliasRequest(CommandEvent e) {
		boolean wasWaitingForAliases = e.isPlayer() ? playersWaitingForAliases.contains(e.getPlayer().getUniqueId()) : playersWaitingForAliases.contains(CONSOLE_UUID);
		if (wasWaitingForAliases) {
			e.sendMessage(Chat.GREEN + "Command Aliases: " + String.join(Chat.GREEN + ", " + Chat.YELLOW, e.getCommand().getAliases()));
			if (e.getSubCommand().isPresent()) {
				e.sendMessage(Chat.GREEN + "Subcommand Aliases: " + String.join(Chat.GREEN + ", " + Chat.YELLOW, e.getSubCommand().get().getAliases()));
			}
			return true;
		}
		return false;
	}

}
