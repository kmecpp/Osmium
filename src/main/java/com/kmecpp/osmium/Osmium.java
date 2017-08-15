package com.kmecpp.osmium;

import java.util.Collection;

import org.bukkit.Bukkit;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.platform.Platform;

public final class Osmium {

	public static final String OSMIUM = "Osmium";

	private Osmium() {
	}

	//
	//	//COMMANDS
	//	public static final void registerCommand(OsmiumCommand command) {
	//		Platform.execute(() -> {
	//			Sponge.getCommandManager().register(OsmiumPlugin.getPlugin(), null, "erg");
	//		}, null);
	//		//		Sponge.getCommandManager().register(plugin, command.getSpec(), command.getAliases());
	//	}
	//
	//	//EVENTS
	//	public static final void registerListener(OsmiumListener listener) {
	//		//		Sponge.getEventManager().registerListeners(plugin, listener);
	//	}
	//
	//	public static final void unregisterListener(OsmiumListener listener) {
	//		//		Sponge.getEventManager().unregisterListeners(listener);
	//	}
	//
	//	public static final boolean postEvent(OsmiumEvent e) {
	//		//		return Sponge.getEventManager().post(e);
	//		return false;
	//	}

	public static Collection<Player> getOnlinePlayers() {
		//		System.out.println(Platform.get(Sponge.getServer().getOnlinePlayers(),
		//				Bukkit.getOnlinePlayers()));
		return null;
		//		ArrayList<Player> players = Platform.get(Sponge.getServer().getOnlinePlayers(), Bukkit.getOnlinePlayers());
	}

	public static Player getPlayer(String name) {
		Bukkit.getPlayer(name);
		return null;
	}

	public static final Platform getPlatform() {
		return Platform.getPlatform();
	}

}
