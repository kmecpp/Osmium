package com.kmecpp.osmium.api.util.lib;

import java.util.Iterator;
import java.util.function.Function;

public class WrappedIterator<T, R> implements Iterable<R> {

	private Iterable<T> source;
	private Function<T, R> mapper;

	public WrappedIterator(Iterable<T> source, Function<T, R> mapper) {
		this.source = source;
		this.mapper = mapper;
	}

	@Override
	public Iterator<R> iterator() {
		Iterator<T> sourceIterator = source.iterator();
		return new Iterator<R>() {

			@Override
			public boolean hasNext() {
				return sourceIterator.hasNext();
			}

			@Override
			public R next() {
				return mapper.apply(sourceIterator.next());
			}

		};
	}

}
