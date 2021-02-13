package com.kmecpp.osmium.api.util;

public class Rational extends Number implements Comparable<Rational> {

	private static final long serialVersionUID = -6424084903680437418L;

	public static final Rational ONE = new Rational(1);
	public static final Rational ZERO = new Rational(0);

	private int numerator;
	private int denominator;

	public Rational(int numerator, int denominator) {
		if (denominator == 0) {
			throw new IllegalArgumentException("denominator is zero");
		}
		if (denominator < 0) {
			numerator *= -1;
			denominator *= -1;
		}
		this.numerator = numerator;
		this.denominator = denominator;
		reduce();
	}

	public Rational(int numerator) {
		this(numerator, 1);
	}

	private void reduce() {
		int gcd = gcd(numerator, denominator);
		numerator /= gcd;
		denominator /= gcd;
	}

	public Rational add(Rational rational) {
		int numerator = (this.numerator * rational.denominator) + (rational.numerator * this.denominator);
		int denominator = this.denominator * rational.denominator;
		return new Rational(numerator, denominator);
	}

	public Rational multiply(Rational rational) {
		return new Rational(numerator * rational.numerator, denominator * rational.denominator);
	}

	public Rational subtract(Rational rational) {
		return add(rational.negate(rational));
	}

	public Rational divide(Rational rational) {
		return multiply(rational.invert(rational));
	}

	public Rational negate(Rational rational) {
		return new Rational(-numerator, denominator);
	}

	public Rational invert(Rational rational) {
		return new Rational(denominator, numerator);
	}

	public Rational add(int number) {
		return add(new Rational(number));
	}

	public Rational subtract(int number) {
		return subtract(new Rational(number));
	}

	public Rational multiply(int number) {
		return multiply(new Rational(number));
	}

	public Rational divide(int number) {
		return divide(new Rational(number));
	}

	public int getNumerator() {
		return this.numerator;
	}

	public int getDenominator() {
		return this.denominator;
	}

	public int round() {
		return Math.round(floatValue());
	}

	public int ceil() {
		return (int) Math.ceil(doubleValue());
	}

	public int floor() {
		return (int) Math.floor(doubleValue());
	}

	@Override
	public byte byteValue() {
		return (byte) this.doubleValue();
	}

	@Override
	public double doubleValue() {
		return ((double) numerator) / ((double) denominator);
	}

	@Override
	public float floatValue() {
		return (float) this.doubleValue();
	}

	@Override
	public int intValue() {
		return (int) this.doubleValue();
	}

	@Override
	public long longValue() {
		return (long) this.doubleValue();
	}

	@Override
	public short shortValue() {
		return (short) this.doubleValue();
	}

	public String format(int digits) {
		return MathUtil.round(doubleValue(), digits);
	}

	@Override
	public String toString() {
		return numerator + "/" + denominator;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rational) {
			return this.compareTo((Rational) obj) == 0;
		} else if (obj instanceof Number) {
			return doubleValue() == ((Number) obj).doubleValue();
		}
		return false;
	}

	@Override
	public int compareTo(Rational frac) {
		long t = this.getNumerator() * frac.getDenominator();
		long f = frac.getNumerator() * this.getDenominator();
		int result = 0;
		if (t > f) {
			result = 1;
		} else if (f > t) {
			result = -1;
		}
		return result;
	}

	private static int gcd(int a, int b) {
		return b == 0 ? a : gcd(b, a % b);
	}

}
