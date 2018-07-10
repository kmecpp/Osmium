package com.kmecpp.osmium.api.tasks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class CountdownTask extends OsmiumTask {

	private int count;
	private int remaining;

	private TaskExecutor tickHandler;

	public CountdownTask(OsmiumPlugin plugin) {
		super(plugin);
	}

	public int getTick() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public TaskExecutor getTickHandler() {
		return tickHandler;
	}

	public void setTickHandler(TaskExecutor tickHandler) {
		this.tickHandler = tickHandler;
	}

	public CountdownTask start() {
		this.remaining = count;
		if (Platform.isBukkit()) {
			this.taskImpl = Bukkit.getScheduler().runTaskTimerAsynchronously((JavaPlugin) plugin.getPluginImplementation(), getCountdown(this), delay, interval);

		} else if (Platform.isSponge()) {
			this.taskImpl = org.spongepowered.api.scheduler.Task.builder()
					.async()
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
				if (task.tickHandler != null) {
					task.tickHandler.execute(task);
				}
				if (--task.remaining <= 0) {
					task.executor.execute(task);
					task.cancel();
				}
			}
		};
	}

}
