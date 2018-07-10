package com.kmecpp.osmium.api.tasks;

import org.bukkit.scheduler.BukkitTask;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public abstract class Task<T extends Task<T>> {

	protected final OsmiumPlugin plugin;

	protected Object taskImpl;

	protected String name;
	protected boolean async;
	protected long delay;
	protected long interval;
	protected TaskExecutor<T> executor;

	public Task(OsmiumPlugin plugin) {
		this.plugin = plugin;
	}

	public OsmiumPlugin getPlugin() {
		return plugin;
	}

	public <I> I getPluginImplemenation() {
		return plugin.getPluginImplementation();
	}

	@SuppressWarnings("unchecked")
	public <I> I getTaskImplementation() {
		return (I) taskImpl;
	}

	public String getName() {
		return name;
	}

	public Task<T> setName(String name) {
		this.name = name;
		return getInstance();
	}

	public long getDelay() {
		return delay;
	}

	public T setDelay(long delay) {
		this.delay = delay;
		return getInstance();
	}

	public long getInterval() {
		return interval;
	}

	public T setInterval(long interval) {
		this.interval = interval;
		return getInstance();
	}

	public T setInterval(long interval, TimeUnit unit) {
		switch (unit) {
		case SECOND:
			this.interval = interval * 20;
			break;
		case MINUTE:
			this.interval = interval * 20 * 60;
			break;
		case HOUR:
			this.interval = interval * 20 * 60 * 60;
			break;
		case DAY:
			this.interval = interval * 20 * 60 * 60 * 24;
			break;
		}
		return getInstance();
	}

	public boolean isAsync() {
		return async;
	}

	public Task<T> setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public TaskExecutor<T> getExecutor() {
		return executor;
	}

	public Task<T> setExecutor(TaskExecutor<T> executor) {
		this.executor = executor;
		return this;
	}

	public abstract Task<T> start();

	public void cancel() {
		if (Platform.isBukkit()) {
			this.<BukkitTask> getTaskImplementation().cancel();
		} else if (Platform.isSponge()) {
			this.<org.spongepowered.api.scheduler.Task> getTaskImplementation().cancel();
		}
	}

	@SuppressWarnings("unchecked")
	protected T getInstance() {
		return (T) this;
	}

}
