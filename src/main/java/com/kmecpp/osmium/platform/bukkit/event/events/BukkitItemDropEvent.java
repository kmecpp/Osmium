package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.player.PlayerDropItemEvent;

import com.kmecpp.osmium.api.event.events.ItemDropEvent;
import com.kmecpp.osmium.platform.BukkitAccess;

public class BukkitItemDropEvent implements ItemDropEvent {

	private PlayerDropItemEvent event;

	public BukkitItemDropEvent(PlayerDropItemEvent event) {
		this.event = event;
	}

	@Override
	public PlayerDropItemEvent getSource() {
		return event;
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}

	public static class BukkitItemDropPlayerEvent extends BukkitItemDropEvent implements ItemDropEvent.Player {

		public BukkitItemDropPlayerEvent(PlayerDropItemEvent event) {
			super(event);
		}

		@Override
		public com.kmecpp.osmium.api.entity.Player getPlayer() {
			return BukkitAccess.getPlayer(super.event.getPlayer());
		}

	}

}
