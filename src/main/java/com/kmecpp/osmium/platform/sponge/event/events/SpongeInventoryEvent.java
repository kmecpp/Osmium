package com.kmecpp.osmium.platform.sponge.event.events;

import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import com.kmecpp.osmium.SpongeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.inventory.ClickType;
import com.kmecpp.osmium.api.inventory.Inventory;

public class SpongeInventoryEvent implements InventoryEvent {

	private InteractInventoryEvent event;

	public SpongeInventoryEvent(InteractInventoryEvent event) {
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
	public Inventory getInventory() {
		return SpongeAccess.getInventory(event.getTargetInventory());
	}

	@Override
	public Player getPlayer() {
		return SpongeAccess.getPlayer((org.spongepowered.api.entity.living.player.Player) event.getSource());
	}

	@Override
	public boolean shouldFire() {
		return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player;
	}

	public static class SpongeInventoryOpenEvent extends SpongeInventoryEvent implements InventoryEvent.Open {

		private InteractInventoryEvent.Open event;

		public SpongeInventoryOpenEvent(InteractInventoryEvent.Open event) {
			super(event);
			this.event = event;
		}

		@Override
		public InteractInventoryEvent.Open getSource() {
			return event;
		}

	}

	public static class SpongeInventoryCloseEvent extends SpongeInventoryEvent implements InventoryEvent.Close {

		private InteractInventoryEvent.Close event;

		public SpongeInventoryCloseEvent(InteractInventoryEvent.Close event) {
			super(event);
			this.event = event;
		}

		@Override
		public InteractInventoryEvent.Close getSource() {
			return event;
		}

	}

	public static class SpongeInventoryClickEvent implements InventoryEvent.Click {

		private ClickInventoryEvent event;

		public SpongeInventoryClickEvent(ClickInventoryEvent event) {
			this.event = event;
		}

		@Override
		public ClickInventoryEvent getSource() {
			return event;
		}

		@Override
		public Inventory getInventory() {
			return SpongeAccess.getInventory(event.getTargetInventory());
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
		public boolean isLeftClick() {
			return event instanceof ClickInventoryEvent.Primary;
		}

		@Override
		public boolean isRightClick() {
			return event instanceof ClickInventoryEvent.Secondary;
		}

		@Override
		public ClickType getClick() {
			return ClickType.fromSource(event.getClass());
		}

		@Override
		public int getSlot() {
			return event.getTransactions().get(0).getSlot().getProperty(SlotIndex.class).get().getValue();
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

}
