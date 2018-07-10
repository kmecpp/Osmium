package com.kmecpp.osmium.platform.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;

public class OsmiumBukkitMain extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
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
