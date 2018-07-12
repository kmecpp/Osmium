package com.kmecpp.osmium.api.database;

import java.lang.Thread.State;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

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
			setName("Database Queue Executor");
			setDaemon(true);
		}

		@Override
		public void run() {
			Connection connection = database.getConnection();
			while (true) {
				try {
					String update = queue.take();
					if (database.isClosed()) {
						connection = database.getConnection();
					}
					connection.createStatement().executeUpdate(update);
					database.update(update);
				} catch (InterruptedException | SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
