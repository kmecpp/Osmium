package com.kmecpp.osmium.api.tasks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.scheduler.Task.Builder;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class CountdownTask extends AbstractTask<CountdownTask> {

	private final int count;
	private volatile int remaining;
	private volatile boolean paused;

	public CountdownTask(OsmiumPlugin plugin, int count) {
		super(plugin);
		this.count = count;
		this.setInterval(1, com.kmecpp.osmium.api.tasks.TimeUnit.SECOND);
	}

	public int getLength() {
		return count;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public boolean isLast() {
		return remaining <= 0;
	}

	public void pause() {
		this.paused = true;
	}

	public void unpause() {
		this.paused = false;
	}

	@Override
	public CountdownTask start() {
		super.start();

		this.remaining = count;
		if (Platform.isBukkit()) {
			if (async) {
				this.taskImpl = Bukkit.getScheduler().runTaskTimerAsynchronously((JavaPlugin) plugin.getPluginImplementation(), getCountdown(this), delay, interval);
			} else {
				this.taskImpl = Bukkit.getScheduler().runTaskTimer((JavaPlugin) plugin.getPluginImplementation(), getCountdown(this), delay, interval);
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
					.submit(plugin.getPluginImplementation());
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
				if (task.remaining <= 0) {
					task.cancel();
				}
				task.executor.execute(task);
				task.remaining--;

			}
		};
	}

}
