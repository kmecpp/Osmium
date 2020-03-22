package com.kmecpp.osmium.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;

public class HashedArrayList<E> implements List<E>, Set<E> {

	private ArrayList<E> list;
	private HashSet<E> set;

	public HashedArrayList() {
		this.list = new ArrayList<>();
		this.set = new HashSet<>();
	}

	public HashedArrayList(Collection<E> collection) {
		this();
		list.addAll(collection);
		set.addAll(collection);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(E e) {
		boolean added = set.add(e);

		if (added) {
			list.add(e);
		}

		return added;
	}

	@Override
	public boolean remove(Object o) {
		boolean removed = set.remove(o);

		if (removed) {
			list.remove(o);
		}

		return removed;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = set.addAll(c);

		if (changed) {
			list.addAll(c);
		}

		return changed;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean changed = set.addAll(c);

		if (changed) {
			list.addAll(index, c);
		}

		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = set.removeAll(c);

		if (changed) {
			list.clear();
			list.addAll(set);
		}

		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = set.retainAll(c);

		if (changed) {
			list.retainAll(c);
		}

		return changed;
	}

	@Override
	public void clear() {
		list.clear();
		set.clear();
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public E set(int index, E element) {
		E result = list.set(index, element);
		if (result != null)
			set.remove(element);

		return result;
	}

	@Override
	public void add(int index, E element) {
		boolean added = set.add(element);

		if (added) {
			list.add(index, element);
		}
	}

	@Override
	public E remove(int index) {
		E result = list.remove(index);

		if (result != null) {
			set.remove(result);
		}

		return result;
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<E> spliterator() {
		return List.super.spliterator();
	}

}
