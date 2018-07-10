package com.kmecpp.osmium.api.tasks;

import org.bukkit.scheduler.BukkitTask;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public abstract class Task {

	protected final OsmiumPlugin plugin;

	protected Object taskImpl;

	protected String name;
	protected boolean async;
	protected long delay;
	protected long interval;
	protected TaskExecutor executor;

	public Task(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public <T> T getPluginImplemenation() {
		return plugin.getPluginImplementation();
	}

	@SuppressWarnings("unchecked")
	public <T> T getTaskImplementation() {
		return (T) taskImpl;
	}

	public String getName() {
		return name;
	}

	public Task setName(String name) {
		this.name = name;
		return this;
	}

	public long getDelay() {
		return delay;
	}

	public Task setDelay(long delay) {
		this.delay = delay;
		return this;
	}

	public long getInterval() {
		return interval;
	}

	public Task setInterval(long interval) {
		this.interval = interval;
		return this;
	}

	public boolean isAsync() {
		return async;
	}

	public Task setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public TaskExecutor getExecutor() {
		return executor;
	}

	public Task setExecutor(TaskExecutor executor) {
		this.executor = executor;
		return this;
	}

	public abstract Task start();

	public void cancel() {
		if (Platform.isBukkit()) {
			this.<BukkitTask> getTaskImplementation().cancel();
		} else if (Platform.isSponge()) {
			this.<org.spongepowered.api.scheduler.Task> getTaskImplementation().cancel();
		}
	}

}
