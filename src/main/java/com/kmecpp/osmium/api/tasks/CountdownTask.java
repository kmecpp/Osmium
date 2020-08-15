package com.kmecpp.osmium.api.tasks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.scheduler.Task.Builder;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class CountdownTask extends AbstractTask<CountdownTask> {

	private final int start;
	private volatile int remaining;
	private volatile boolean paused;

	public CountdownTask(OsmiumPlugin plugin, int start) { //Number of times executed is start + 1
		super(plugin);
		this.start = start;
		this.setInterval(1, com.kmecpp.osmium.api.tasks.TimeUnit.SECOND);
		this.setMaxRuns(start);
	}

	public int getStart() {
		return start;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public void pause() {
		this.paused = true;
	}

	public void unpause() {
		this.paused = false;
	}

	@Override
	public boolean isLastRun() {
		return remaining == 0;
	}

	@Override
	public CountdownTask start() {
		super.start();

		this.remaining = start;
		if (Platform.isBukkit()) {
			if (async) {
				this.taskImpl = Bukkit.getScheduler().runTaskTimerAsynchronously((JavaPlugin) plugin.getSource(), getCountdown(this), delay, interval);
			} else {
				this.taskImpl = Bukkit.getScheduler().runTaskTimer((JavaPlugin) plugin.getSource(), getCountdown(this), delay, interval);
			}
		} else if (Platform.isSponge()) {
			Builder builder = org.spongepowered.api.scheduler.Task.builder();
			if (async) {
				builder.async();
			}
			this.taskImpl = builder
					.delay(delay * 50, TimeUnit.MILLISECONDS)
					.interval(interval * 50, TimeUnit.MILLISECONDS)
					.execute(getCountdown(this))
					.submit(plugin.getSource());
		}
		return this;
	}

	private static Runnable getCountdown(CountdownTask task) {
		return new Runnable() {

			@Override
			public void run() {
				if (task.paused) {
					return;
				}

				try {
					if (task.executor != null) {
						task.executor.execute(task);
					} else {
						OsmiumLogger.warn("Ran countdown task with null executor: " + task);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				task.remaining--;
				task.counter++;

				if (task.remaining < 0) {
					task.cancel();
				}
			}

		};
	}

}
