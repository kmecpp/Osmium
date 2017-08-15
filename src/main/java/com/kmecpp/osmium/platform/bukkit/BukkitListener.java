package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.PlayerManager;
import com.kmecpp.osmium.api.event.events.PlayerChatEvent;

public class BukkitListener implements Listener {

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		Osmium.getEventManager().callEvent(new PlayerChatEvent(PlayerManager.fromBukkitPlayer(e.getPlayer()), e.getMessage()));
	}

}
