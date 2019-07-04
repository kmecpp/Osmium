package com.kmecpp.osmium.api.tasks;

import java.util.HashMap;

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

	protected boolean running;

	protected HashMap<String, Object> data;

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

	public int getDelay() {
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

	public int getInterval() {
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

	public T setAsync(boolean async) {
		this.async = async;
		return getInstance();
	}

	public AbstractTask<T> setTime(int delay, int interval, TimeUnit unit) {
		this.delay = delay * unit.getTickValue();
		this.interval = interval * unit.getTickValue();
		return getInstance();
	}

	public TaskExecutor<T> getExecutor() {
		return executor;
	}

	public T setExecutor(TaskExecutor<T> executor) {
		this.executor = executor;
		return getInstance();
	}

	public <D> D getData(String key, D defaultValue) {
		D data = getData(key);
		return data != null ? data : defaultValue;
	}

	@SuppressWarnings("unchecked")
	public <D> D getData(String key) {
		if (data == null) {
			return null;
		}
		return (D) data.get(key);
	}

	public void setData(String key, Object value) {
		if (data == null) {
			data = new HashMap<>();
		}
		data.put(key, value);
	}

	public boolean isRunning() {
		return running;
	}

	public T start() {
		if (running) {
			throw new IllegalStateException("Already running!");
		}
		running = true;
		return getInstance();
	}

	public void cancel() {
		if (!running) {
			throw new IllegalStateException("Not running!");
		}
		running = false;
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
