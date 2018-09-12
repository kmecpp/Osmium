package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.BlockEvent;
import com.kmecpp.osmium.platform.bukkit.event.BukkitEvent;

public class BukkitBlockEvent {

	public static class BukkitBlockBreakEvent extends BukkitEvent<org.bukkit.event.block.BlockBreakEvent> implements BlockEvent.Break {

		public BukkitBlockBreakEvent(org.bukkit.event.block.BlockBreakEvent event) {
			super(event);
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
