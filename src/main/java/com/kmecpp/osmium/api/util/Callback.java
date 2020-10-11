package com.kmecpp.osmium.api.util;

import java.util.function.Consumer;

public class Callback {

	private Consumer<Integer> consumer;
	private Runnable runnable;

	public void onComplete(Consumer<Integer> callback) {
		this.consumer = callback;
	}

	public void onComplete(Runnable callback) {
		this.runnable = callback;
	}

	public void complete(int rowsUpdated) {
		if (runnable != null) {
			runnable.run();
		}
		if (consumer != null) {
			consumer.accept(rowsUpdated);
		}
	}

}
