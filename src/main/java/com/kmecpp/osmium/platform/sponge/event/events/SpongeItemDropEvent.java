package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.item.inventory.DropItemEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.event.events.ItemDropEvent;

public class SpongeItemDropEvent implements ItemDropEvent {

	private DropItemEvent event;

	public SpongeItemDropEvent(DropItemEvent event) {
		this.event = event;
	}

	@Override
	public Object getSource() {
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

	public static class SpongeItemDropPlayerEvent extends SpongeItemDropEvent implements ItemDropEvent.Player {

		public SpongeItemDropPlayerEvent(DropItemEvent event) {
			super(event);
		}

		@Override
		public com.kmecpp.osmium.api.entity.Player getPlayer() {
			return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) super.event.getSource());
		}

		@Override
		public boolean shouldFire() {
			return super.event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
		}

	}

}
