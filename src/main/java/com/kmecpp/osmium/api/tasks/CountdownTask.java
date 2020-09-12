package com.kmecpp.osmium.api.tasks;

import com.kmecpp.osmium.api.plugin.OsmiumPlugin;

public class CountdownTask extends AbstractOsmiumTask<CountdownTask> {

	private final int start;
	private volatile int remaining;
	private volatile boolean paused;

	public CountdownTask(OsmiumPlugin plugin, int start) { //Number of times executed is start + 1
		super(plugin);
		this.start = start;
		this.remaining = start + 1;
		this.setLastRun(start);
		this.setInterval(1, com.kmecpp.osmium.api.TickTimeUnit.SECOND);
	}

	public int getStart() {
		return start;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public void pause() {
		this.paused = true;
	}

	public void unpause() {
		this.paused = false;
	}

	@Override
	protected void doExecute() {
		if (paused) {
			return;
		}

		super.doExecute();
		remaining--;
	}

	//	@Override
	//	public CountdownTask start() {
	//		super.start();
	//
	//		this.remaining = start;
	//		if (Platform.isBukkit()) {
	//			if (async) {
	//				this.taskImpl = Bukkit.getScheduler().runTaskTimerAsynchronously((JavaPlugin) plugin.getSource(), getCountdown(this), delay, interval);
	//			} else {
	//				this.taskImpl = Bukkit.getScheduler().runTaskTimer((JavaPlugin) plugin.getSource(), getCountdown(this), delay, interval);
	//			}
	//		} else if (Platform.isSponge()) {
	//			Builder builder = org.spongepowered.api.scheduler.Task.builder();
	//			if (async) {
	//				builder.async();
	//			}
	//			this.taskImpl = builder
	//					.delay(delay * 50, TimeUnit.MILLISECONDS)
	//					.interval(interval * 50, TimeUnit.MILLISECONDS)
	//					.execute(getCountdown(this))
	//					.submit(plugin.getSource());
	//		}
	//		return this;
	//	}
	//
	//	private static Runnable getCountdown(CountdownTask task) {
	//		return new Runnable() {
	//
	//			@Override
	//			public void run() {
	//				if (task.paused) {
	//					return;
	//				}
	//
	//				try {
	//					if (task.executor != null) {
	//						task.executor.execute(task);
	//					} else {
	//						OsmiumLogger.warn("Ran countdown task with null executor: " + task);
	//					}
	//				} catch (Throwable t) {
	//					t.printStackTrace();
	//				}
	//				task.remaining--;
	//				task.counter++;
	//
	//				if (task.remaining < 0) {
	//					task.cancel();
	//				}
	//			}
	//
	//		};
	//	}

}
