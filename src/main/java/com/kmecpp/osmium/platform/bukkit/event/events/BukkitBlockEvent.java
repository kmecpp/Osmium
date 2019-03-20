package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.BlockEvent;

public class BukkitBlockEvent {

	public static class BukkitBlockBreakEvent implements BlockEvent.Break {

		private BlockBreakEvent event;

		public BukkitBlockBreakEvent(BlockBreakEvent event) {
			this.event = event;
		}

		@Override
		public BlockBreakEvent getSource() {
			return event;
		}

		@Override
		public Block getBlock() {
			return BukkitAccess.getBlock(event.getBlock());
		}

		@Override
		public Player getPlayer() {
			return BukkitAccess.getPlayer(event.getPlayer());
		}

	}

	public static class BukkitBlockPlaceEvent implements BlockEvent.Break {

		private BlockPlaceEvent event;

		public BukkitBlockPlaceEvent(BlockPlaceEvent event) {
			this.event = event;
		}

		@Override
		public BlockPlaceEvent getSource() {
			return event;
		}

		@Override
		public Block getBlock() {
			return BukkitAccess.getBlock(event.getBlock());
		}

		@Override
		public Player getPlayer() {
			return BukkitAccess.getPlayer(event.getPlayer());
		}

	}

	public static class BukkitPlayerChangeBlockEvent implements BlockEvent.PlayerChange {

		private BlockBreakEvent breakEvent;
		private BlockPlaceEvent placeEvent;

		public BukkitPlayerChangeBlockEvent(org.bukkit.event.block.BlockEvent event) {
			if (event instanceof BlockBreakEvent) {
				this.breakEvent = (BlockBreakEvent) event;
			} else if (event instanceof BlockPlaceEvent) {
				this.placeEvent = (BlockPlaceEvent) event;
			}
		}

		@Override
		public org.bukkit.event.block.BlockEvent getSource() {
			return breakEvent != null ? breakEvent : placeEvent;
		}

		@Override
		public Block getBlock() {
			return BukkitAccess.getBlock(getSource().getBlock());
		}

		@Override
		public Player getPlayer() {
			return BukkitAccess.getPlayer(breakEvent != null ? breakEvent.getPlayer() : placeEvent.getPlayer());
		}

	}

}
