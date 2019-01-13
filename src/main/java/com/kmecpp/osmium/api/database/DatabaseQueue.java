package com.kmecpp.osmium.api.database;

import java.lang.Thread.State;
import java.util.concurrent.LinkedBlockingQueue;

import org.hibernate.Session;

import com.kmecpp.osmium.Osmium;

public class DatabaseQueue {

	private final Database database;
	private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
	private final QueueExecutor executor = new QueueExecutor();

	public DatabaseQueue(Database database) {
		this.database = database;
	}

	public void start() {
		if (executor.getState() == State.NEW) {
			executor.start();
		}
	}

	public void flush() {
		while (!queue.isEmpty()) {
			try {
				database.update(queue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void queue(String update) {
		if (Osmium.isShuttingDown()) {
			database.update(update);
		} else {
			queue.add(update);
		}
	}

	public class QueueExecutor extends Thread {

		public QueueExecutor() {
			setName("Osmium Database Queue");
			setDaemon(true);
		}

		@Override
		public void run() {
			Session session = database.getSession();
			while (true) {
				try {
					String update = queue.take();
					session.createNativeQuery(update).executeUpdate();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
