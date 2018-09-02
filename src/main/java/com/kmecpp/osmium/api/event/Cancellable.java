package com.kmecpp.osmium.api.event;

public interface Cancellable {

	boolean isCancelled();

	void setCancelled(boolean cancel);

}
