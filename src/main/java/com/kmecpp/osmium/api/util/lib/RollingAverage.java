package com.kmecpp.osmium.api.util.lib;

public class RollingAverage {

	private final double[] samples;
	private int sampleCount;
	private int index; //Points to next empty index

	private double total;

	public RollingAverage(int windowSize) {
		this.samples = new double[windowSize];
	}

	public void add(double sample) {
		if (sampleCount >= samples.length) {
			total -= samples[index];
		} else {
			sampleCount++;
		}
		samples[index] = sample;
		index = (index + 1) % samples.length;
		total += sample;
	}

	public double mean() {
		return sampleCount == 0 ? 0 : total / sampleCount;
	}

	public double mean(int maxSamples) {
		if (maxSamples >= sampleCount) {
			return mean();
		}

		double sum = 0;
		int count = 0;
		for (int i = 0; i < sampleCount && i < maxSamples; i++) {
			sum += samples[Math.floorMod((index - 1) - i, samples.length)];
			count++;
		}
		return count == 0 ? 0 : sum / count;
	}

	@Override
	public String toString() {
		return samples.toString();
	}

}
