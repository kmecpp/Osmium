package com.kmecpp.osmium.api.util;

public class Pair<X, Y> {

	private X first;
	private Y second;

	public Pair(X first, Y second) {
		this.first = first;
		this.second = second;
	}

	public static <X, Y> Pair<X, Y> of(X first, Y second) {
		return new Pair<>(first, second);
	}

	public X getFirst() {
		return first;
	}

	public Y getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Pair) {
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
