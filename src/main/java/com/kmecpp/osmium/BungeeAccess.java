package com.kmecpp.osmium;

import com.kmecpp.osmium.api.command.Command;
import com.kmecpp.osmium.api.command.CommandManager;
import com.kmecpp.osmium.api.command.CommandSender;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.platform.bungee.BungeeGenericCommandSender;
import com.kmecpp.osmium.platform.bungee.BungeePlayer;
import com.kmecpp.osmium.platform.osmium.CommandRedirectSender;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

public class BungeeAccess {

	public static void registerListener(OsmiumPlugin plugin, Listener listener) {
		BungeeCord.getInstance().getPluginManager().registerListener(plugin.getSource(), listener);
	}

	public static Player getPlayer(ProxiedPlayer player) {
		return PlayerList.getPlayer(player);
	}

	public static void processConsoleCommand(String command) {
		BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), command);
	}

	public static void processConsoleCommand(CommandSender receiver, String command) {
		CommandRedirectSender sender = new CommandRedirectSender(new BungeeGenericCommandSender(BungeeCord.getInstance().getConsole()), receiver);
		Osmium.getCommandManager().processCommand(sender, command);
	}

	public static void processCommand(net.md_5.bungee.api.CommandSender sender, String command) {
		BungeeCord.getInstance().getPluginManager().dispatchCommand(sender, command);
	}

	public static void registerCommand(OsmiumPlugin plugin, Command command) {
		try {
			net.md_5.bungee.api.plugin.Command bungeeCommand = new net.md_5.bungee.api.plugin.Command(command.getPrimaryAlias(), command.getPermission(), command.getAliases()) {

				@Override
				public void execute(net.md_5.bungee.api.CommandSender bungeeSender, String[] args) {
					CommandSender sender = bungeeSender instanceof ProxiedPlayer ? new BungeePlayer((ProxiedPlayer) bungeeSender) : new BungeeGenericCommandSender(bungeeSender);
					CommandManager.invokeCommand(command, sender, command.getPrimaryAlias(), args);
				}

			};
			BungeeCord.getInstance().getPluginManager().registerCommand(plugin.getSource(), bungeeCommand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
