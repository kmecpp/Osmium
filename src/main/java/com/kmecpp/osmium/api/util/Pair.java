package com.kmecpp.osmium.api.util;

public class Pair<X, Y> {

	private X first;
	private Y second;

	public Pair(X first, Y second) {
		this.first = first;
		this.second = second;
	}

	public X getFirst() {
		return first;
	}

	public Y getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		if (first != null && second != null) {
			return first.hashCode() ^ second.hashCode();
		}

		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<?, ?> pair = (Pair<?, ?>) obj;
			return first.equals(pair.first) && second.equals(pair.second);
		}

		return false;
	}

	@Override
	public String toString() {
		return "<" + first + ", " + second + ">";
	}

}
