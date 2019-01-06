//package com.kmecpp.osmium;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.Objects;
//
//import com.google.common.base.Function;
//import com.kmecpp.osmium.api.Abstraction;
//
//public class WrappedArray<S, A extends Abstraction> implements Collection<A> {
//
//	private S[] source;
//	private Function<S, A> wrapper;
//
//	public WrappedArray(S[] source, Function<S, A> wrapper) {
//		this.source = source;
//		this.wrapper = wrapper;
//	}
//
//	@Override
//	public int size() {
//		return source.length;
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return source.length == 0;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void clear() {
//		source = (S[]) Array.newInstance(source.getClass().getComponentType(), 0);
//	}
//
//	@Override
//	public boolean contains(Object obj) {
//		for (S sourceObj : source) {
//			if (obj instanceof Abstraction ? Objects.equals(sourceObj, ((Abstraction) obj).getSource()) : Objects.equals(sourceObj, obj)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public Iterator<A> iterator() {
//		return new Iter();
//	}
//
//	private class Iter implements Iterator<A> {
//
//		private int index;
//
//		@Override
//		public boolean hasNext() {
//			return index < source.length;
//		}
//
//		@Override
//		public A next() {
//			return wrapper.apply(source[index++]);
//		}
//
//	}
//
//	@Override
//	public Object[] toArray() {
//		Object[] arr = new Object[source.length];
//		int i = 0;
//		for (S sourceElement : source) {
//			arr[i] = wrapper.apply(sourceElement);
//			i++;
//		}
//		return arr;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public <T> T[] toArray(T[] a) {
//		if (a.length < source.length) {
//			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), source.size());
//		}
//		int i = 0;
//		for (S s : source) {
//			a[i] = (T) wrapper.apply(s);
//			i++;
//		}
//		for (i = source.length; i < a.length; i++) {
//			a[i] = null;
//		}
//		return a;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public boolean add(A e) {
//		Collections.unmodifiableList(list)
//		return source.add((S) e.getSource());
//	}
//
//	@Override
//	public boolean remove(Object obj) {
//		return obj instanceof Abstraction ? source.remove(((Abstraction) obj).getSource()) : source.remove(obj);
//	}
//
//	@Override
//	public boolean containsAll(Collection<?> c) {
//		for (Object obj : c) {
//			if (!contains(obj)) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	@Override
//	public boolean addAll(Collection<? extends A> collection) {
//		for (A abstraction : collection) {
//			add(abstraction);
//		}
//		return !collection.isEmpty();
//	}
//
//	@Override
//	public boolean removeAll(Collection<?> c) {
//		boolean modified = false;
//		for (Object obj : c) {
//			if (remove(obj)) {
//				modified = true;
//			}
//		}
//		return modified;
//	}
//
//	@Override
//	public boolean retainAll(Collection<?> c) {
//		boolean modified = false;
//		Iterator<A> iterator = this.iterator();
//		while (iterator.hasNext()) {
//			A obj = iterator.next();
//			if (!c.contains(obj) && !c.contains(obj.getSource())) {
//				iterator.remove();
//				modified = true;
//			}
//		}
//		return modified;
//	}
//
//}
