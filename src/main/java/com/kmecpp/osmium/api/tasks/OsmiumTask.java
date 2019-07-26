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
				taskImpl = Bukkit.getScheduler().runTaskTimerAsynchronously(getPluginImplemenation(), () -> executor.execute(this), delay, interval == 0 ? -1 : interval);
			} else {
				taskImpl = Bukkit.getScheduler().runTaskTimer(getPluginImplemenation(), () -> executor.execute(this), delay, interval == 0 ? -1 : interval);
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
					.execute((t) -> executor.execute(this))
					.submit(getPluginImplemenation());
		}
		return this;
	}

}
