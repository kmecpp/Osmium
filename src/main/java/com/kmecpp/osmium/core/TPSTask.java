package com.kmecpp.osmium.core;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.kmecpp.osmium.api.TickTimeUnit;
import com.kmecpp.osmium.api.tasks.Schedule;

public class TPSTask {

	//	private static long lastTick = System.nanoTime();
	//	private static float lastTps;
	//
	//	private static int sampleIndex;
	//	private static float[] samples = new float[20 * 30];
	//	private static boolean samplesFilled;

	private static double tps = 20;
	private static long lastTick = System.nanoTime();

	private static double[] samples = new double[60 * 20]; //60 seconds
	private static int sampleIndex;
	private static int totalTicks;

	static {
		Arrays.fill(samples, 20);

		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			//				Osmium.getOnlinePlayers().stream().re
			//				double tps = TPSTask.tps;
			double asyncTps = 2e9 / (double) (System.nanoTime() - lastTick);
			if (asyncTps < 20) {
				TPSTask.tps = asyncTps;
				//				System.out.println("ASYNC TIME: " + ((System.nanoTime() - lastTick) / 1000F));
				//					System.out.println("ASYNC: " + asyncTps);
			}
			//				else {
			//					System.out.println("USING SYNC: " + TPSTask.tps);
			//				}

			sampleIndex = ++sampleIndex % samples.length;
			samples[sampleIndex] = TPSTask.tps;

			totalTicks++;
			if (totalTicks == Integer.MAX_VALUE) { //After 1242 days...
				totalTicks = samples.length;
			}
		}, 1000, 50, java.util.concurrent.TimeUnit.MILLISECONDS);
	}

	@Schedule(delay = 5, interval = 1, unit = TickTimeUnit.TICK)
	public static void run() {
		if (lastTick == 0) {
			lastTick = System.nanoTime();
		}

		double currentTps = 2e9 / (double) (System.nanoTime() - lastTick);
		//		System.out.println(currentTps + ", " + getRecentTPS() + ", " + getTPS() + ", " + getMinuteTPS());
		if (currentTps < 20.3) {
			tps = currentTps;
		}
		lastTick = System.nanoTime();
	}

	//	/*
	//	 * Add valid samples
	//	 */
	//	@Schedule(delay = 100, interval = 1, async = true)
	//	public static void runAsync() {
	//
	//	}

	public static double getLastTickSpeed() {
		return tps;
	}

	public static double getAverage(int seconds) {
		int ticks = Math.min(samples.length, seconds * 20);
		if (totalTicks < ticks) {
			ticks = totalTicks;
		}
		double sum = 0;
		for (int i = 0; i < ticks; i++) {
			sum += samples[Math.floorMod(sampleIndex - i, samples.length)];
		}
		return sum / ticks;
	}

	//	private static float calculateAsyncTPS() {
	//		float asyncTps = (20 * 50 * 1000000F) / (System.nanoTime() - lastTick);
	//		return asyncTps > 30 ? 30 : asyncTps;
	//	}
	//
	//	public static float getCurrentTPS() {
	//		float asyncTps = calculateAsyncTPS();
	//		if (asyncTps < 20) {
	//			return asyncTps;
	//		}
	//		return lastTps;
	//	}
	//
	//	/**
	//	 * @return the average TPS for the last 30 seconds
	//	 */
	//	public static float getAverageTPS() {
	//		float sum = 0;
	//		if (samplesFilled) {
	//			for (int i = 0; i < samples.length; i++) {
	//				sum += samples[i];
	//			}
	//			return sum / (samples.length);
	//		} else if (sampleIndex > 0) {
	//			for (int i = 0; i < sampleIndex; i++) {
	//				sum += samples[i];
	//			}
	//			return sum / sampleIndex;
	//		} else {
	//			return 20F;
	//		}
	//	}

}
