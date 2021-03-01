package com.kmecpp.osmium;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Function;
import com.kmecpp.osmium.api.Abstraction;

public class Wrappers {

	public static <S, A extends Abstraction> Collection<A> convert(Iterable<S> source, Function<S, A> converter) {
		ArrayList<A> list = new ArrayList<>();
		for (S sourceObj : source) {
			list.add(converter.apply(sourceObj));
		}
		return list;
	}

	//	public static <S, A extends Abstraction> Collection<A> convert(Collection<S> source, Function<S, A> converter) {
	//		ArrayList<A> list = new ArrayList<>();
	//		for (S sourceObj : source) {
	//			list.add(converter.apply(sourceObj));
	//		}
	//		return list;
	//	}

	public static <S, A extends Abstraction> Collection<A> convert(S[] source, Function<S, A> converter) {
		ArrayList<A> list = new ArrayList<>();
		for (S sourceObj : source) {
			list.add(converter.apply(sourceObj));
		}
		return list;
	}

}
