package com.kmecpp.osmium.api.event.events;

import com.kmecpp.osmium.api.event.Cancellable;
import com.kmecpp.osmium.api.event.PlayerEvent;
import com.kmecpp.osmium.api.inventory.ItemStack;

public interface PlayerInteractEvent extends PlayerEvent, Cancellable {

	public interface Item extends PlayerInteractEvent {

		ItemStack getItemStack();

	}

	public interface Block extends PlayerInteractEvent {

		com.kmecpp.osmium.api.Block getBlock();

	}

	public interface Entity extends PlayerInteractEvent {

		com.kmecpp.osmium.api.entity.Entity getEntity();

	}

	public interface Physical extends PlayerInteractEvent {

	}

}
