package com.kmecpp.osmium.platform.sponge.event.events;

import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.inventory.ClickType;
import com.kmecpp.osmium.api.inventory.Inventory;
import com.kmecpp.osmium.platform.SpongeAccess;

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

	public static class SpongeInventoryInteractEvent extends SpongeInventoryEvent implements InventoryEvent.Interact {

		private ClickInventoryEvent event;

		public SpongeInventoryInteractEvent(ClickInventoryEvent event) {
			super(event);
			this.event = event;
		}

		@Override
		public InteractInventoryEvent getSource() {
			return event;
		}

		@Override
		public boolean isClick() {
			return !(event instanceof ClickInventoryEvent.Drag);
		}

		@Override
		public boolean isDrag() {
			return event instanceof ClickInventoryEvent.Drag;
		}

	}

	public static class SpongeInventoryClickEvent extends SpongeInventoryInteractEvent implements InventoryEvent.Click {

		private ClickInventoryEvent event;

		public SpongeInventoryClickEvent(ClickInventoryEvent event) {
			super(event);
			this.event = event;
		}

		@Override
		public ClickInventoryEvent getSource() {
			return event;
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
		public boolean shouldFire() {
			return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player && !(event instanceof ClickInventoryEvent.Drag);
		}

	}

	public static class SpongeInventoryDragEvent extends SpongeInventoryInteractEvent implements InventoryEvent.Drag {

		private ClickInventoryEvent.Drag event;

		public SpongeInventoryDragEvent(ClickInventoryEvent.Drag event) {
			super(event);
			this.event = event;
		}

		@Override
		public ClickInventoryEvent.Drag getSource() {
			return event;
		}

		@Override
		public boolean isEvenDrag() {
			return event instanceof ClickInventoryEvent.Drag.Primary;
		}

		@Override
		public boolean isSingleDrag() {
			return event instanceof ClickInventoryEvent.Drag.Secondary;
		}

		@Override
		public Set<Integer> getSlots() {
			return event.getTransactions().stream()
					.map(t -> t.getSlot().getProperty(SlotIndex.class).get().getValue())
					.collect(Collectors.toSet());
		}

		@Override
		public boolean shouldFire() {
			return event.getSource() instanceof org.spongepowered.api.entity.living.player.Player
					&& event instanceof ClickInventoryEvent.Drag;
		}

	}

}
