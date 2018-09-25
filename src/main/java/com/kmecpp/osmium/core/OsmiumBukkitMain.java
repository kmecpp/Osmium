package com.kmecpp.osmium.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.BukkitPlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.bukkit.BukkitWorld;

public class OsmiumBukkitMain extends BukkitPlugin {

	@Override
	public void onDisable() {
		super.onDisable();
		Osmium.shutdown();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent e) {
		PlayerList.addPlayer(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerList.removePlayer(e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldLoad(WorldInitEvent e) {
		WorldList.addWorld(new BukkitWorld(e.getWorld()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldUnload(WorldUnloadEvent e) {
		WorldList.removeWorld(e.getWorld().getName());
	}

}
