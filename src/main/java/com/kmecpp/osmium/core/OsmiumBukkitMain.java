package com.kmecpp.osmium.core;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.plugin.BukkitPlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.bukkit.BukkitWorld;

public class OsmiumBukkitMain extends BukkitPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		Osmium.shutdown();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) { //PlayerQuitEvent is not always called if PlayerLoginEvent is called
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent e) {
		WorldList.removeWorld(e.getWorld().getName());
	}

}
