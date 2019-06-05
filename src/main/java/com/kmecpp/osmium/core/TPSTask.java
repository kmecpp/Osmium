package com.kmecpp.osmium.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.kmecpp.osmium.api.tasks.Schedule;
import com.kmecpp.osmium.api.tasks.TimeUnit;

public class TPSTask {

	//	private static long lastTick = System.nanoTime();
	//	private static float lastTps;
	//
	//	private static int sampleIndex;
	//	private static float[] samples = new float[20 * 30];
	//	private static boolean samplesFilled;

	private static long lastTick;
	private static int sampleIndex;
	private static double[] samples = new double[60 * 20];
	private static double tps = 20;

	static {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				//				Osmium.getOnlinePlayers().stream().re
				//				double tps = TPSTask.tps;
				double asyncTps = 2e9 / (double) (System.nanoTime() - lastTick);
				if (asyncTps < 20) {
					tps = asyncTps;
					//					System.out.println("ASYNC: " + asyncTps);
				}
				//				else {
				//					System.out.println("USING SYNC: " + TPSTask.tps);
				//				}

				sampleIndex = ++sampleIndex % samples.length;
				samples[sampleIndex] = TPSTask.tps;
			}

		}, 0, 50, java.util.concurrent.TimeUnit.MILLISECONDS);
	}

	@Schedule(delay = 5, interval = 1, unit = TimeUnit.TICK)
	public static void run() {
		if (lastTick == 0) {
			lastTick = System.nanoTime();
		}

		double currentTps = 2e9 / (double) (System.nanoTime() - lastTick);
		//		System.out.println(currentTps + ", " + getRecentTPS() + ", " + getTPS() + ", " + getMinuteTPS());
		if (currentTps < 21) {
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
		seconds = Math.max(seconds, samples.length);
		double sum = 0;
		for (int i = 0; i < seconds * 20; i++) {
			sum += samples[Math.floorMod(sampleIndex - i, samples.length)];
		}
		return sum / seconds;
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
