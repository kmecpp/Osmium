package com.kmecpp.osmium.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class RegisteredListener {

	private Object instance;
	private Method method;

	public RegisteredListener(Object instance, Method method) {
		this.instance = instance;
		this.method = method;
	}

	public void call(Event event) {
		try {
			method.invoke(instance, event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			OsmiumLogger.error("Error occurred while executing event!");
			e.printStackTrace();
		}
	}

}
