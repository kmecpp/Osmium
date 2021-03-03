package com.kmecpp.osmium.core;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.BungeePlugin;
import com.kmecpp.osmium.cache.PlayerList;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;

public class OsmiumBungeeMain extends BungeePlugin {

	@Override
	public void onDisable() {
		super.onDisable();
		Osmium.shutdown();
	}

	@EventHandler
	public void onPlayerLogin(net.md_5.bungee.api.event.PostLoginEvent e) {
		PlayerList.addPlayer(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent e) {
		PlayerList.removePlayer(e.getPlayer().getName());
	}

}
