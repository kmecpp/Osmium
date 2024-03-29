package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.inventory.ClickType;
import com.kmecpp.osmium.api.inventory.ItemStack;
import com.kmecpp.osmium.platform.BukkitAccess;

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

	public ClickType getClickType() {
		return event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK ? ClickType.LEFT
				: event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ? ClickType.RIGHT
						: null;
	}

	public static class BukkitPlayerInteractItemEvent extends BukkitPlayerInteractEvent implements PlayerInteractEvent.Item {

		public BukkitPlayerInteractItemEvent(org.bukkit.event.player.PlayerInteractEvent event) {
			super(event);
		}

		@Override
		public boolean shouldFire() {
			return event.hasItem();
		}

		@Override
		public ItemStack getItemStack() {
			return BukkitAccess.getItemStack(event.getItem());
		}

	}

	public static class BukkitPlayerInteractBlockEvent extends BukkitPlayerInteractEvent implements PlayerInteractEvent.Block {

		public BukkitPlayerInteractBlockEvent(org.bukkit.event.player.PlayerInteractEvent event) {
			super(event);
		}

		@Override
		public com.kmecpp.osmium.api.Block getBlock() {
			return BukkitAccess.getBlock(event.getClickedBlock());
		}

		@Override
		public boolean shouldFire() {
			return event.hasBlock();
		}

	}

	public static class BukkitPlayerInteractPhysicalEvent extends BukkitPlayerInteractEvent implements PlayerInteractEvent.Block {

		public BukkitPlayerInteractPhysicalEvent(org.bukkit.event.player.PlayerInteractEvent event) {
			super(event);
		}

		@Override
		public com.kmecpp.osmium.api.Block getBlock() {
			return BukkitAccess.getBlock(event.getClickedBlock());
		}

		@Override
		public boolean shouldFire() {
			return event.hasBlock() && event.getAction() == Action.PHYSICAL;
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
		public ClickType getClickType() {
			return leftClickEvent != null ? ClickType.LEFT : ClickType.RIGHT;
		}

		@Override
		public com.kmecpp.osmium.api.entity.Entity getEntity() {
			return BukkitAccess.getEntity(leftClickEvent != null ? leftClickEvent.getEntity() : rightClickEvent.getRightClicked());
		}

		@Override
		public boolean shouldFire() {
			return leftClickEvent != null
					? leftClickEvent.getDamager() instanceof org.bukkit.entity.Player
					: true;
		}

	}

}
