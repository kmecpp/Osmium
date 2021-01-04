package com.kmecpp.osmium.api.database.sqlite;

import java.lang.Thread.State;
import java.util.concurrent.LinkedBlockingQueue;

import com.kmecpp.osmium.Osmium;

public class DatabaseQueue {

	//	private final SQLDatabase database;
	private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	private final QueueExecutor executor = new QueueExecutor();

	//	public DatabaseQueue(SQLDatabase database) {
	//		this.database = database;
	//	}

	public void start() {
		if (executor.getState() == State.NEW) {
			executor.start();
		}
	}

	public void flush() {
		while (!queue.isEmpty()) {
			try {
				queue.take().run();
				//				database.update(queue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void submit(Runnable runnable) {
		if (Osmium.isShuttingDown()) {
			runnable.run();
		} else {
			queue.add(runnable);
		}
	}

	//	public void queue(String update) {
	//		if (Osmium.isShuttingDown()) {
	//			database.update(update);
	//		} else {
	//			queue.add(update);
	//		}
	//	}

	public class QueueExecutor extends Thread {

		public QueueExecutor() {
			setName("Database Queue Executor");
			setDaemon(true);
		}

		@Override
		public void run() {
			//			Connection connection = database.getConnection();
			while (true) {
				try {
					Runnable runnable = queue.take();
					runnable.run();

					//					String update = queue.take();
					//					if (database.isClosed()) {
					//						connection = database.getConnection();
					//					}
					//					System.out.println("EXECUTING UPDATE: " + update);
					//					connection.createStatement().executeUpdate(update);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
