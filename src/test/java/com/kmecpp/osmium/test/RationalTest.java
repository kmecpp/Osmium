package com.kmecpp.osmium.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kmecpp.osmium.api.util.Rational;

public class RationalTest {

	@Test
	public void test() {
		Rational r1 = new Rational(1, 2);
		Rational r2 = new Rational(1, 2);
		Rational r3 = new Rational(1, 6);

		assertEquals(r1, r1);
		assertEquals(r1, r2);
		assertEquals(r1.add(r3), new Rational(4, 6));
		assertEquals(r1.add(r3), new Rational(2, 3));
		assertEquals(r1.subtract(r3), new Rational(1, 3));
		assertEquals(r1.subtract(r3), new Rational(2, 6));
		assertEquals(r3.subtract(r1), new Rational(-1, 3));
		assertEquals(r1.multiply(r3), new Rational(1, 12));
		assertEquals(r1.divide(r3), new Rational(3));
	}

}
