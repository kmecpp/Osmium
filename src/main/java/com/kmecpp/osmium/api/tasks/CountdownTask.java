package com.kmecpp.osmium.api.tasks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class CountdownTask extends AbstractTask<CountdownTask> {

	private final int count;

	private int remaining;

	public CountdownTask(OsmiumPlugin plugin, int count) {
		super(plugin);
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public int getRemaining() {
		return remaining;
	}

	public boolean isLast() {
		return remaining <= 0;
	}

	@Override
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
				task.remaining--;
				task.executor.execute(task);
				if (task.remaining <= 0) {
					task.cancel();
				}
			}
		};
	}

}
