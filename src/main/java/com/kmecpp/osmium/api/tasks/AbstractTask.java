package com.kmecpp.osmium.api.tasks;

import org.bukkit.scheduler.BukkitTask;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public abstract class AbstractTask<T extends AbstractTask<T>> {

	protected final OsmiumPlugin plugin;

	protected Object taskImpl;

	protected String name;
	protected boolean async;
	protected int delay;
	protected int interval;
	protected TaskExecutor<T> executor;

	public AbstractTask(OsmiumPlugin plugin) {
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

	public AbstractTask<T> setName(String name) {
		this.name = name;
		return getInstance();
	}

	public long getDelay() {
		return delay;
	}

	public T setDelay(int delay) {
		this.delay = delay;
		return getInstance();
	}

	public T setDelay(int delay, TimeUnit unit) {
		this.delay = delay * unit.getTickValue();
		return getInstance();
	}

	public long getInterval() {
		return interval;
	}

	public T setInterval(int interval) {
		this.interval = interval;
		return getInstance();
	}

	public T setInterval(int interval, TimeUnit unit) {
		this.interval = interval * unit.getTickValue();
		return getInstance();
	}

	public boolean isAsync() {
		return async;
	}

	public AbstractTask<T> setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public AbstractTask<T> setTime(int delay, int interval, TimeUnit unit) {
		this.delay = delay * unit.getTickValue();
		this.interval = interval * unit.getTickValue();
		return getInstance();
	}

	public TaskExecutor<T> getExecutor() {
		return executor;
	}

	public AbstractTask<T> setExecutor(TaskExecutor<T> executor) {
		this.executor = executor;
		return this;
	}

	public abstract T start();

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
