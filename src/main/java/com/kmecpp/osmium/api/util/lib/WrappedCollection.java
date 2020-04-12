package com.kmecpp.osmium.api.util.lib;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;
import com.kmecpp.osmium.api.Abstraction;

public class WrappedCollection<S, A extends Abstraction> implements Collection<A> {

	private Collection<S> source;
	private Function<S, A> wrapper;

	public WrappedCollection(Collection<S> source, Function<S, A> wrapper) {
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
	public void clear() {
		source.clear();
	}

	@Override
	public boolean contains(Object obj) {
		return obj instanceof Abstraction ? source.contains(((Abstraction) obj).getSource()) : source.contains(obj);
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

	@Override
	public Object[] toArray() {
		Object[] arr = new Object[source.size()];
		int i = 0;
		for (S sourceElement : source) {
			arr[i] = wrapper.apply(sourceElement);
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
		for (i = source.size(); i < a.length; i++) {
			a[i] = null;
		}
		return a;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(A e) {
		return source.add((S) e.getSource());
	}

	@Override
	public boolean remove(Object obj) {
		return obj instanceof Abstraction ? source.remove(((Abstraction) obj).getSource()) : source.remove(obj);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends A> collection) {
		for (A abstraction : collection) {
			add(abstraction);
		}
		return !collection.isEmpty();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object obj : c) {
			if (remove(obj)) {
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<A> iterator = this.iterator();
		while (iterator.hasNext()) {
			A obj = iterator.next();
			if (!c.contains(obj) && !c.contains(obj.getSource())) {
				iterator.remove();
				modified = true;
			}
		}
		return modified;
	}

}
