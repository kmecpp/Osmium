package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;

public class BukkitPlayerInteractEvent implements PlayerInteractEvent {

	protected org.bukkit.event.player.PlayerInteractEvent event;

	public BukkitPlayerInteractEvent(org.bukkit.event.player.PlayerInteractEvent event) {
		this.event = event;
	}

	@Override
	public org.bukkit.event.player.PlayerInteractEvent getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return BukkitAccess.getPlayer(event.getPlayer());
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}

	public static class BukkitPlayerInteractItemEvent extends BukkitPlayerInteractEvent implements PlayerInteractEvent.Item {

		public BukkitPlayerInteractItemEvent(org.bukkit.event.player.PlayerInteractEvent event) {
			super(event);
		}

		@Override
		public boolean shouldFire() {
			return event.hasItem();
		}

	}

	public static class BukkitPlayerInteractBlockEvent extends BukkitPlayerInteractEvent implements PlayerInteractEvent.Block {

		public BukkitPlayerInteractBlockEvent(org.bukkit.event.player.PlayerInteractEvent event) {
			super(event);
		}

		@Override
		public boolean shouldFire() {
			return event.hasBlock();
		}

	}

	public static class BukkitPlayerInteractEntityEvent implements PlayerInteractEvent.Entity {

		private EntityDamageByEntityEvent leftClickEvent;
		private org.bukkit.event.player.PlayerInteractEntityEvent rightClickEvent;

		public BukkitPlayerInteractEntityEvent(Event event) {
			if (event instanceof EntityDamageByEntityEvent) {
				this.leftClickEvent = (EntityDamageByEntityEvent) event;
			} else if (event instanceof PlayerInteractEntityEvent) {
				this.rightClickEvent = (PlayerInteractEntityEvent) event;
			} else {
				throw new IllegalArgumentException("Invalid event source: " + event);
			}
		}

		@Override
		public Event getSource() {
			return leftClickEvent != null ? leftClickEvent : rightClickEvent;
		}

		@Override
		public Player getPlayer() {
			return BukkitAccess.getPlayer(leftClickEvent != null ? (org.bukkit.entity.Player) leftClickEvent.getDamager() : rightClickEvent.getPlayer());
		}

		@Override
		public boolean isCancelled() {
			return leftClickEvent != null ? leftClickEvent.isCancelled() : rightClickEvent.isCancelled();
		}

		@Override
		public void setCancelled(boolean cancel) {
			if (leftClickEvent != null) {
				leftClickEvent.setCancelled(cancel);
			} else {
				rightClickEvent.setCancelled(cancel);
			}
		}

		@Override
		public boolean shouldFire() {
			return leftClickEvent != null
					? leftClickEvent.getDamager() instanceof org.bukkit.entity.Player
					: true;
		}

	}

}
