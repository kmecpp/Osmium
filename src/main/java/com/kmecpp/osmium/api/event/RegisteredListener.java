package com.kmecpp.osmium.api.event;

import java.lang.reflect.Method;

import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class RegisteredListener implements Comparable<RegisteredListener> {

	private Object instance;
	private Method method;
	private Order order;

	public RegisteredListener(Object instance, Method method, Order order) {
		this.instance = instance;
		this.method = method;
		this.order = order;
	}

	public Object getInstance() {
		return instance;
	}

	public Method getMethod() {
		return method;
	}

	public Order getOrder() {
		return order;
	}

	public void call(Event event) {
		try {
			method.invoke(instance, event);
		} catch (Throwable e) {
			OsmiumLogger.error("Error occurred while executing event!");
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(RegisteredListener l) {
		return order.ordinal() - l.order.ordinal();
	}

}
