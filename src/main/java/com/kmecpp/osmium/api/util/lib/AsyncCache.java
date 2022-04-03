package com.kmecpp.osmium.api.util.lib;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.MilliTimeUnit;

public class AsyncCache<T> { //TODO: This has a lot of room for improvement. Might also want a RestrictedAsyncCache that requires a key (ex: player ID) with the get() and only accepts one of the same key at a time

	private final Supplier<T> supplier;
	private final long maxAge;

	private T value;
	private long lastUpdate;

	public AsyncCache(Supplier<T> supplier, long maxAge, MilliTimeUnit unit) {
		this.supplier = supplier;
		this.maxAge = maxAge * unit.getMillisecondTime();
	}

	public void get(Consumer<T> consumer) {
		Osmium.getTask().setAsync(true).setExecutor(t -> {
			long currentTime = System.currentTimeMillis();

			synchronized (this) { //Synchronization here prevents the supplier from being invoked by multiple requesters at the same time. This is still entirely off the main thread
				if (currentTime == 0 || currentTime >= lastUpdate + maxAge) {
					try {
						this.value = supplier.get();
					} catch (Throwable throwable) {
						throwable.printStackTrace();
					}
					this.lastUpdate = currentTime;
				}
			}

			consumer.accept(value);
		}).start();
	}

	//	private static final ExecutorService threadPool = Executors.newFixedThreadPool(3);
	//
	//	public static void main(String[] args) {
	//		AsyncCache<Integer> test = new AsyncCache<>(new Supplier<Integer>() {
	//
	//			int i = 1;
	//
	//			@Override
	//			public Integer get() {
	//				try {
	//					Thread.sleep(20);
	//				} catch (InterruptedException e) {
	//					e.printStackTrace();
	//				}
	//				return i++;
	//			}
	//
	//		}, 100, MilliTimeUnit.MILLISECOND);
	//
	//		for (int i = 0; i < 30; i++) {
	//			try {
	//				Thread.sleep(5);
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//			test.get(v -> System.out.println(v));
	//		}
	//		threadPool.shutdown();
	//	}

}
