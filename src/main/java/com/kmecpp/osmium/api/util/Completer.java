package com.kmecpp.osmium.api.util;

public class Completer {

	private Runnable completer;

	public void onComplete(Runnable completer) {
		this.completer = completer;
	}

	public void complete() {
		if (completer != null) {
			completer.run();
		}
	}

}
