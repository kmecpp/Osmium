package com.kmecpp.osmium.api.config;

public class ConfigWriteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConfigWriteException(String message) {
		super(message);
	}

	public ConfigWriteException(Throwable t) {
		super(t);
	}

	public ConfigWriteException(String message, Throwable t) {
		super(message, t);
	}

}
