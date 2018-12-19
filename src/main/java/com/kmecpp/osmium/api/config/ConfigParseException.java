package com.kmecpp.osmium.api.config;

public class ConfigParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConfigParseException(String message) {
		super(message);
	}

	public ConfigParseException(Throwable t) {
		super(t);
	}

	public ConfigParseException(String message, Throwable t) {
		super(message, t);
	}

}
