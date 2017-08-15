package com.kmecpp.osmium.api.event;

public interface EventExecutor {

	public void execute(Listener listener, Event event) throws Throwable;

}
