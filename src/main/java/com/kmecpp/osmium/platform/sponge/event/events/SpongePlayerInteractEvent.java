package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerInteractEvent;
import com.kmecpp.osmium.api.inventory.ItemStack;

public class SpongePlayerInteractEvent implements PlayerInteractEvent {

	protected InteractEvent event;

	public SpongePlayerInteractEvent(InteractEvent event) {
		this.event = event;
	}

	@Override
	public InteractEvent getSource() {
		return event;
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
	}

	@Override
	public boolean shouldFire() {
		return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
	}

	public static class SpongePlayerInteractItemEvent extends SpongePlayerInteractEvent implements PlayerInteractEvent.Item {

		private InteractItemEvent event;

		public SpongePlayerInteractItemEvent(InteractItemEvent event) {
			super(event);
			this.event = event;
		}

		@Override
		public ItemStack getItemStack() {
			return SpongeAccess.getItemStack(event.getItemStack().createStack());
		}

	}

	public static class SpongePlayerInteractBlockEvent extends SpongePlayerInteractEvent implements PlayerInteractEvent.Block {

		private InteractBlockEvent event;

		public SpongePlayerInteractBlockEvent(InteractBlockEvent event) {
			super(event);
			this.event = event;
		}

		@Override
		public com.kmecpp.osmium.api.Block getBlock() {
			return SpongeAccess.getBlock(event.getTargetBlock().getLocation().get());
		}

	}

	public static class SpongePlayerInteractPhysicalEvent implements PlayerInteractEvent.Block {

		private ChangeBlockEvent.Modify event;

		public SpongePlayerInteractPhysicalEvent(ChangeBlockEvent.Modify event) {
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

		@Override
		public com.kmecpp.osmium.api.Block getBlock() {
			return SpongeAccess.getBlock(event.getTransactions().get(0).getOriginal().getLocation().get());
		}

		@Override
		public Player getPlayer() {
			return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
		}

		@Override
		public boolean shouldFire() {
			return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player
					&& event.getTransactions().size() == 1;
		}

	}

	public static class SpongePlayerInteractEntityEvent extends SpongePlayerInteractEvent implements PlayerInteractEvent.Entity {

		private InteractEntityEvent event;

		public SpongePlayerInteractEntityEvent(InteractEntityEvent event) {
			super(event);
			this.event = event;
		}

		@Override
		public com.kmecpp.osmium.api.entity.Entity getEntity() {
			return SpongeAccess.getEntity(event.getTargetEntity());
		}

	}

}
