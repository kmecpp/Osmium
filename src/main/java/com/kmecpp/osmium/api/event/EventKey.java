package com.kmecpp.osmium.api.event;

public class EventKey {

	private Class<?> eventClass;
	private Order order;

	public EventKey(Class<?> eventClass, Order order) {
		this.eventClass = eventClass;
		this.order = order;
	}

	public Class<?> getEventClass() {
		return eventClass;
	}

	public Order getOrder() {
		return order;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EventKey) {
			EventKey code = (EventKey) obj;
			return eventClass == code.eventClass && order == code.order;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return eventClass.hashCode() & order.hashCode();
	}

}
