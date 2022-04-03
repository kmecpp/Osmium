package com.kmecpp.osmium.api.database;

import java.util.concurrent.LinkedBlockingQueue;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.logging.OsmiumLogger;

public class DatabaseQueue {

	private final LinkedBlockingQueue<ContextRunnable> queue = new LinkedBlockingQueue<>();
	private final QueueExecutor executor = new QueueExecutor();
	private boolean started;

	public synchronized void start() {
		if (!started) {
			started = true;
			executor.start();
		}
	}

	public void flush() {
		while (!queue.isEmpty()) {
			try {
				queue.take().runnable.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void submit(Runnable runnable) {
		if (Osmium.isShuttingDown()) {
			runnable.run();
		} else {
			queue.add(new ContextRunnable(runnable));
		}
	}

	public class QueueExecutor extends Thread {

		public QueueExecutor() {
			setName("Database Queue Executor");
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				try {
					ContextRunnable contextRunnable = queue.take();

					try {
						contextRunnable.runnable.run();
					} catch (Throwable t) {
						t.printStackTrace();
						OsmiumLogger.warn("Caused By:"); //Display stack trace of where the ContextRunnable was added to the queue
						for (StackTraceElement element : contextRunnable.stack) {
							System.err.println(element);
						}
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	private static class ContextRunnable {

		private Runnable runnable;
		private StackTraceElement[] stack;

		public ContextRunnable(Runnable runnable) {
			this.runnable = runnable;
			this.stack = Thread.currentThread().getStackTrace();
		}

	}

}
