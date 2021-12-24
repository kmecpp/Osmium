package com.kmecpp.osmium.api.util.lib;

public class Money {

	private long dollars;
	private int cents;

	public Money(long dollars, int cents) {
		this.dollars = dollars;
		this.cents = cents;
	}

	public long getDollars() {
		return dollars;
	}

	public int getCents() {
		return cents;
	}

}