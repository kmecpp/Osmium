package com.kmecpp.osmium.platform.bukkit.event;

import com.kmecpp.osmium.api.event.Event;

public abstract class BukkitEvent<T extends org.bukkit.event.Event> implements Event {

	protected T event;

	public BukkitEvent(T event) {
		this.event = event;
	}

	@Override
	public T getSource() {
		return event;
	}

}
