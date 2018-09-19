package com.kmecpp.osmium.platform.bukkit.event.events;

import org.bukkit.event.block.BlockBreakEvent;

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
			return (Player) event.getPlayer();
		}

	}

}
