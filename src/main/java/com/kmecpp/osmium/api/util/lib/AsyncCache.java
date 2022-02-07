package com.kmecpp.osmium.api.util.lib;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.MilliTimeUnit;

public class AsyncCache<T> {

	private final Supplier<T> supplier;
	private final long maxAge;

	private T value;
	private long lastUpdate;

	public AsyncCache(Supplier<T> supplier) {
		this(supplier, 5, MilliTimeUnit.MINUTE);
	}

	public AsyncCache(Supplier<T> supplier, long maxAge) {
		this(supplier, maxAge, MilliTimeUnit.MILLISECOND);
	}

	public AsyncCache(Supplier<T> supplier, long maxAge, MilliTimeUnit unit) {
		this.supplier = supplier;
		this.maxAge = maxAge * unit.getMillisecondTime();
	}

	public void get(Consumer<T> consumer) {
		Osmium.getTask().setAsync(true).setExecutor(t -> {
			long currentTime = System.currentTimeMillis();
			if (currentTime == 0 || currentTime >= lastUpdate + maxAge) {
				try {
					this.value = supplier.get();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
				this.lastUpdate = currentTime;
			}

			consumer.accept(value);
		}).start();
	}

}
