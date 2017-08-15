package com.kmecpp.osmium.api.event;

public class RegisteredListener {

	private Listener listener;
	private EventExecutor executor;

	public RegisteredListener(Listener listener, EventExecutor executor) {
		this.listener = listener;
		this.executor = executor;
	}

	public Class<? extends Listener> getListenerClass() {
		return this.listener.getClass();
	}

	public void callEvent(Event event) throws Throwable {
		executor.execute(listener, event);
	}

	@Override
	public String toString() {
		return listener.getClass().getSimpleName();
	}

}
