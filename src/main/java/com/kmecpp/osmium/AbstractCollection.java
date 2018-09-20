package com.kmecpp.osmium;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;
import com.kmecpp.osmium.api.Abstraction;

public class AbstractCollection<S, A extends Abstraction> implements Collection<A> {

	private Collection<S> source;
	private Function<S, A> wrapper;

	public AbstractCollection(Collection<S> source, Function<S, A> wrapper) {
		this.source = source;
		this.wrapper = wrapper;
	}

	@Override
	public int size() {
		return source.size();
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty();
	}

	@Override
	public boolean contains(Object obj) {
		return obj instanceof Abstraction ? source.contains((Abstraction) obj) : source.contains(obj);
	}

	@Override
	public Iterator<A> iterator() {
		return new Iter();
	}

	private class Iter implements Iterator<A> {

		private Iterator<S> sourceIter = source.iterator();

		@Override
		public boolean hasNext() {
			return sourceIter.hasNext();
		}

		@Override
		public A next() {
			return wrapper.apply(sourceIter.next());
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public A[] toArray() {
		A[] arr = (A[]) new Abstraction[source.size()];
		int i = 0;
		for (S s : source) {
			arr[i] = wrapper.apply(s);
			i++;
		}
		return arr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < source.size()) {
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), source.size());
		}
		int i = 0;
		for (S s : source) {
			a[i] = (T) wrapper.apply(s);
			i++;
		}

		if (a.length > source.size()) {
			a[source.size()] = null;
		}
		return a;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(A e) {
		return source.add((S) e.getSource());
	}

	@Override
	public boolean remove(Object o) {
		return o instanceof Abstraction ? source.remove((Abstraction) o) : source.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends A> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
		source.clear();
	}

}
