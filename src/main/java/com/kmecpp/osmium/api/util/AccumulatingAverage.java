package com.kmecpp.osmium.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.kmecpp.osmium.api.persistence.Serialization;

public class AccumulatingAverage {

	//averagenew=averageold+valuenew−averageoldsizenew
	private BigDecimal average;
	private int amount;

	static {
		Serialization.register(AccumulatingAverage.class, AccumulatingAverage::fromString);
	}

	public AccumulatingAverage() {
		this(1);
	}

	public AccumulatingAverage(double initial) {
		this(new BigDecimal(initial), 1);
	}

	public AccumulatingAverage(BigDecimal average, int amount) {
		this.average = average;
		this.amount = amount;
	}

	public double asDouble() {
		return average.doubleValue();
	}

	public AccumulatingAverage add(double d) {
		amount++;
		average = average.add(new BigDecimal(d).subtract(average).divide(new BigDecimal(amount), RoundingMode.HALF_UP));
		return this;
	}

	public BigDecimal getAverage() {
		return average;
	}

	public static AccumulatingAverage fromString(String str) {
		try {
			String[] parts = str.split("\\|");
			return new AccumulatingAverage(new BigDecimal(parts[0]), Integer.parseInt(parts[1]));
		} catch (Exception e) {
			return new AccumulatingAverage();
		}
	}

	@Override
	public String toString() {
		return average + "|" + amount;
	}

}
