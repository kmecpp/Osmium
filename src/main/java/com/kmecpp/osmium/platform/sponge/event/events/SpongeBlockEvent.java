package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.Block;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.BlockEvent;

public class SpongeBlockEvent implements BlockEvent {

	private ChangeBlockEvent event;

	public SpongeBlockEvent(ChangeBlockEvent event) {
		this.event = event;
	}

	@Override
	public ChangeBlockEvent getSource() {
		return event;
	}

	@Override
	public Block getBlock() {
		return SpongeAccess.getBlock(event.getTransactions().get(0).getOriginal().getLocation().get());
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}

	public static class SpongeBlockBreakEvent extends SpongeBlockEvent implements BlockEvent.Break {

		private ChangeBlockEvent.Break event;

		public SpongeBlockBreakEvent(org.spongepowered.api.event.block.ChangeBlockEvent.Break event) {
			super(event);
			this.event = event;
		}

		@Override
		public Player getPlayer() {
			return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
		}

		@Override
		public boolean shouldFire() {
			return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
		}

		@Override
		public ChangeBlockEvent.Break getSource() {
			return event;
		}

	}

	public static class SpongeBlockPlaceEvent extends SpongeBlockEvent implements BlockEvent.Place {

		private ChangeBlockEvent.Place event;

		public SpongeBlockPlaceEvent(org.spongepowered.api.event.block.ChangeBlockEvent.Place event) {
			super(event);
			this.event = event;
		}

		@Override
		public ChangeBlockEvent.Place getSource() {
			return event;
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

	public static class SpongePlayerChangeBlockEvent extends SpongeBlockEvent implements BlockEvent.PlayerChange {

		private ChangeBlockEvent event;

		public SpongePlayerChangeBlockEvent(org.spongepowered.api.event.block.ChangeBlockEvent event) {
			super(event);
			this.event = event;
		}

		@Override
		public ChangeBlockEvent getSource() {
			return event;
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
