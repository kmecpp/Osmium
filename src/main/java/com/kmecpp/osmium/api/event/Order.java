package com.kmecpp.osmium.api.event;

import org.bukkit.event.EventPriority;

import com.kmecpp.osmium.api.Abstraction;
import com.kmecpp.osmium.api.platform.Platform;

public enum Order implements Abstraction {

	PRE,
	FIRST,
	EARLY,
	DEFAULT,
	LATE,
	LAST,
	POST,

	;

	private Object source;

	static {
		for (Order order : values()) {
			if (Platform.isBukkit()) {
				switch (order) {
				case PRE:
					order.source = EventPriority.LOWEST;
					break;
				case FIRST:
					order.source = EventPriority.LOWEST;
					break;
				case EARLY:
					order.source = EventPriority.LOW;
					break;
				case DEFAULT:
					order.source = EventPriority.NORMAL;
					break;
				case LATE:
					order.source = EventPriority.HIGH;
					break;
				case LAST:
					order.source = EventPriority.HIGHEST;
					break;
				case POST:
					order.source = EventPriority.MONITOR;
					break;
				}
			} else if (Platform.isSponge()) {
				switch (order) {
				case PRE:
					order.source = org.spongepowered.api.event.Order.PRE;
					break;
				case FIRST:
					order.source = org.spongepowered.api.event.Order.FIRST;
					break;
				case EARLY:
					order.source = org.spongepowered.api.event.Order.EARLY;
					break;
				case DEFAULT:
					order.source = org.spongepowered.api.event.Order.DEFAULT;
					break;
				case LATE:
					order.source = org.spongepowered.api.event.Order.LATE;
					break;
				case LAST:
					order.source = org.spongepowered.api.event.Order.LAST;
					break;
				case POST:
					order.source = org.spongepowered.api.event.Order.POST;
					break;
				}
			}
		}
	}

	@Override
	public Object getSource() {
		return source;
	}

}
