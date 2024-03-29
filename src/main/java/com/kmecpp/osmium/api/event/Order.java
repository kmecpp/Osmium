package com.kmecpp.osmium.api.event;

import org.bukkit.event.EventPriority;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.Abstraction;

public enum Order implements Abstraction {

	/*
	 * THESE CONSTANTS MUST BE DECLARED IN THE ORDER THEY ARE EXECUTED.
	 * RegisteredListener depends on it for comparing
	 */
	FIRST,
	EARLY,
	DEFAULT,
	LATE,
	LAST,
	POST,

	;

	private Object source;

	static {
		if (Platform.isBukkit()) {
			FIRST.source = EventPriority.LOWEST;
			EARLY.source = EventPriority.LOW;
			DEFAULT.source = EventPriority.NORMAL;
			LATE.source = EventPriority.HIGH;
			LAST.source = EventPriority.HIGHEST;
			POST.source = EventPriority.MONITOR;
		} else if (Platform.isSponge()) {
			FIRST.source = org.spongepowered.api.event.Order.FIRST;
			EARLY.source = org.spongepowered.api.event.Order.EARLY;
			DEFAULT.source = org.spongepowered.api.event.Order.DEFAULT;
			LATE.source = org.spongepowered.api.event.Order.LATE;
			LAST.source = org.spongepowered.api.event.Order.LAST;
			POST.source = org.spongepowered.api.event.Order.POST;
		} else if (Platform.isProxy()) {
			FIRST.source = (byte) (net.md_5.bungee.event.EventPriority.LOWEST - 1);
			EARLY.source = (byte) (net.md_5.bungee.event.EventPriority.LOW);
			DEFAULT.source = (byte) (net.md_5.bungee.event.EventPriority.NORMAL);
			LATE.source = (byte) (net.md_5.bungee.event.EventPriority.HIGH);
			LAST.source = (byte) (net.md_5.bungee.event.EventPriority.HIGHEST);
			POST.source = (byte) (net.md_5.bungee.event.EventPriority.HIGHEST + 1);
		}
	}

	@Override
	public Object getSource() {
		return source;
	}

}
