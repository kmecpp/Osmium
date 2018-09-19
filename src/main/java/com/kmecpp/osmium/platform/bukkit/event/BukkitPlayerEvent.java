//package com.kmecpp.osmium.platform.bukkit.event;
//
//import org.bukkit.event.player.PlayerEvent;
//
//import com.kmecpp.osmium.BukkitAccess;
//import com.kmecpp.osmium.api.entity.Player;
//
//public class BukkitPlayerEvent<T extends PlayerEvent> extends BukkitEvent<T> {
//
//	public BukkitPlayerEvent(T event) {
//		super(event);
//	}
//
//	public Player getPlayer() {
//		return BukkitAccess.getPlayer(event.getPlayer());
//	}
//
//}
