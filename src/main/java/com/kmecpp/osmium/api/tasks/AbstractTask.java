package com.kmecpp.osmium.api.tasks;

import org.bukkit.scheduler.BukkitTask;

import com.kmecpp.osmium.api.platform.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public abstract class AbstractTask<T extends AbstractTask<T>> {

	protected final OsmiumPlugin plugin;

	protected Object taskImpl;

	protected String name;
	protected boolean async;
	protected long delay;
	protected long interval;
	protected TaskExecutor<T> executor;

	protected TimeUnit unit = TimeUnit.TICK;

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
		return unit.getTickValue() * delay;
	}

	public T setDelay(long delay) {
		this.delay = delay;
		return getInstance();
	}

	public T setDelay(long delay, TimeUnit unit) {
		//		this.delay = getTicks(delay, unit);
		this.delay = delay;
		this.unit = unit;
		return getInstance();
	}

	public long getInterval() {
		return unit.getTickValue() * interval;
	}

	public T setInterval(long interval) {
		this.interval = interval;
		return getInstance();
	}

	public T setInterval(long interval, TimeUnit unit) {
		//		this.interval = getTicks(interval, unit);
		this.interval = interval;
		this.unit = unit;
		return getInstance();
	}

	//	private static long getTicks(long time, TimeUnit unit) {
	//		switch (unit) {
	//		case SECOND:
	//			return time * 20;
	//		case MINUTE:
	//			return time * 20 * 60;
	//		case HOUR:
	//			return time * 20 * 60 * 60;
	//		case DAY:
	//			return time * 20 * 60 * 60 * 24;
	//		default:
	//			throw new IllegalArgumentException("Unknown time unit: " + unit);
	//		}
	//	}

	public boolean isAsync() {
		return async;
	}

	public AbstractTask<T> setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public AbstractTask<T> setUnit(TimeUnit unit) {
		this.unit = unit;
		return this;
	}

	public TaskExecutor<T> getExecutor() {
		return executor;
	}

	public AbstractTask<T> setExecutor(TaskExecutor<T> executor) {
		this.executor = executor;
		return this;
	}

	public abstract AbstractTask<T> start();

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
