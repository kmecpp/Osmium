package com.kmecpp.osmium.platform.bukkit.event.events;

import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.event.events.BlockEvent;
import com.kmecpp.osmium.platform.bukkit.event.BukkitEvent;

public class BukkitBlockEvent extends BukkitEvent<org.bukkit.event.block.BlockEvent> implements BlockEvent {

	@Override
	public Block getBlock() {
		return Blockevent.getBlock();
	}

}
