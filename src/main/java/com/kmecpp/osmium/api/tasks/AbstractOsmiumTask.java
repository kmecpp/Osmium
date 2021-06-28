package com.kmecpp.osmium.api.tasks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.spongepowered.api.scheduler.Task.Builder;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

import net.md_5.bungee.BungeeCord;

public abstract class AbstractOsmiumTask<T extends AbstractTask<T>> extends AbstractTask<T> {

	public AbstractOsmiumTask(OsmiumPlugin plugin) {
		super(plugin);
	}

	@Override
	public T start() {
		super.start();

		if (Platform.isBukkit()) {
			if (async) {
				taskImpl = Bukkit.getScheduler().runTaskTimerAsynchronously(getSource(), this::doExecute, delay, interval == 0 ? -1 : interval);
			} else {
				taskImpl = Bukkit.getScheduler().runTaskTimer(getSource(), this::doExecute, delay, interval == 0 ? -1 : interval);
			}
		} else if (Platform.isSponge()) {
			Builder builder = org.spongepowered.api.scheduler.Task.builder();
			if (name != null) {
				builder.name(name);
			}
			if (async) {
				builder.async();
			}
			taskImpl = builder.delay(delay * 50, TimeUnit.MILLISECONDS)
					.interval(interval * 50, TimeUnit.MILLISECONDS)
					.execute((t) -> this.doExecute())
					.submit(getSource());
		} else if (Platform.isBungeeCord()) {
			taskImpl = BungeeCord.getInstance().getScheduler().schedule(getSource(), this::doExecute, delay * 50, interval * 50, TimeUnit.MILLISECONDS);
		}
		return getInstance();
	}

	protected void doExecute() {
		try {
			executor.execute(getInstance());
		} catch (Throwable t) {
			doFinalize();
			if (cancelOnError) {
				cancel();
			} else {
				throw new RuntimeException(t);
			}
		}
		if (lastRun > 0 && counter >= lastRun) {
			cancel(); //Cancel should be called first to ensure that if the finalizer errors we still exit
			doFinalize();
		}
		counter++;
	}

	protected void doFinalize() {
		if (finalizer != null) {
			finalizer.execute(getInstance());
		}
	}

}
