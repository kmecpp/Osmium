package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.BukkitPlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;

public class OsmiumBukkitMain extends BukkitPlugin {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		Osmium.shutdown();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent e) {
		PlayerList.addPlayer(new BukkitPlayer(e.getPlayer()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerList.removePlayer(e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldLoad(WorldLoadEvent e) {
		WorldList.addWorld(new BukkitWorld(e.getWorld()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldUnload(WorldUnloadEvent e) {
		WorldList.removeWorld(e.getWorld().getName());
	}

}
