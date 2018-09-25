package com.kmecpp.osmium.core;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.plugin.Plugin;

import com.kmecpp.osmium.AppInfo;
import com.kmecpp.osmium.api.plugin.SpongePlugin;
import com.kmecpp.osmium.cache.PlayerList;
import com.kmecpp.osmium.cache.WorldList;
import com.kmecpp.osmium.platform.sponge.SpongeWorld;

@Plugin(name = AppInfo.NAME, id = AppInfo.ID, version = AppInfo.VERSION,
		authors = { AppInfo.AUTHOR }, description = AppInfo.DESCRIPTION, url = AppInfo.URL)
public class OsmiumSpongeMain extends SpongePlugin {

	//	@Listener(order = Order.PRE)
	//	public void onGameInitialization(GameInitializationEvent e) {
	//		Sponge.getEventManager().registerListeners(this, this);
	//	}

	@Listener(order = Order.PRE)
	public void onPlayerLogin(ClientConnectionEvent.Join e) {
		PlayerList.addPlayer(e.getTargetEntity());
	}

	@Listener(order = Order.POST)
	public void onPlayerQuit(ClientConnectionEvent.Disconnect e) {
		PlayerList.removePlayer(e.getTargetEntity().getName());
	}

	@Listener(order = Order.PRE)
	public void onWorldLoad(LoadWorldEvent e) {
		WorldList.addWorld(new SpongeWorld(e.getTargetWorld()));
	}

	@Listener(order = Order.POST)
	public void onWorldUnload(UnloadWorldEvent e) {
		WorldList.removeWorld(e.getTargetWorld().getName());
	}

}
