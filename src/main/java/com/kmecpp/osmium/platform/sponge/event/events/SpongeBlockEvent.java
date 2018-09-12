package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.BlockEvent;
import com.kmecpp.osmium.platform.sponge.event.SpongeEvent;

public class SpongeBlockEvent {

	public static class SpongeBlockBreakEvent extends SpongeEvent<ChangeBlockEvent.Break> implements BlockEvent.Break {

		public SpongeBlockBreakEvent(org.spongepowered.api.event.block.ChangeBlockEvent.Break event) {
			super(event);
		}

		@Override
		public Block getBlock() {
			return SpongeAccess.getBlock(event.getTransactions().get(0).getOriginal().getLocation().get());
		}

		@Override
		public Player getPlayer() {
			return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
		}

		@Override
		public boolean shouldFire() {
			return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
		}

	}

	public static class SpongeBlockPlaceEvent extends SpongeEvent<ChangeBlockEvent.Place> implements BlockEvent.Place {

		public SpongeBlockPlaceEvent(org.spongepowered.api.event.block.ChangeBlockEvent.Place event) {
			super(event);
		}

		@Override
		public Block getBlock() {
			return SpongeAccess.getBlock(event.getTransactions().get(0).getOriginal().getLocation().get());
		}

		@Override
		public Player getPlayer() {
			return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
		}

		@Override
		public boolean shouldFire() {
			return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
		}

	}

}
