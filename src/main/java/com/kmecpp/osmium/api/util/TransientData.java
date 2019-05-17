package com.kmecpp.osmium.api.util;

public class TransientData<T> {

	private T data;
	private long timestamp;
	private long expiration;

	public TransientData(T data, long expiration) {
		this.data = data;
		this.timestamp = System.currentTimeMillis();
		this.expiration = expiration;
	}

	public T getData() {
		return data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getExpiration() {
		return expiration;
	}

	public boolean isExpired() {
		return getTimeLeft() > expiration;
	}

	public long getTimeLeft() {
		return System.currentTimeMillis() - timestamp;
	}

	@Override
	public String toString() {
		return "[" + data + ", " + getTimeLeft() + "ms]";
	}

}
