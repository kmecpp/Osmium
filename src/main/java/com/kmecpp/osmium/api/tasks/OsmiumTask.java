package com.kmecpp.osmium.api.tasks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.spongepowered.api.scheduler.Task.Builder;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class OsmiumTask extends AbstractTask<OsmiumTask> {

	public OsmiumTask(OsmiumPlugin plugin) {
		super(plugin);
	}

	@Override
	public OsmiumTask start() {
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
					.execute((t) -> {
						if (cancelOnError) {
							try {
								doExecute();
							} catch (Throwable throwable) {
								t.cancel();
							}
						} else {
							doExecute();
						}
					})
					.submit(getSource());
		}
		return this;
	}

	private void doExecute() {
		try {
			executor.execute(this);
		} catch (Throwable t) {
			doFinalize();
			throw new RuntimeException(t);
		}
		counter++;
		if (maxRuns > 0 && counter >= maxRuns) {
			cancel(); //Cancel should be called first to ensure that if the finalizer errors we still exit
			doFinalize();
		}
	}

	private void doFinalize() {
		if (finalizer != null) {
			finalizer.execute(this);;
		}
	}

}
