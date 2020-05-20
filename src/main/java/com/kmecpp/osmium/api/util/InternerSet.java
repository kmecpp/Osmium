package com.kmecpp.osmium.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class InternerSet<E> implements Set<E> {

	private HashMap<E, E> map = new HashMap<>();

	public E intern(E sample) {
		return map.get(sample);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return map.keySet().toArray(a);
	}

	@Override
	public boolean add(E e) {
		if (!map.containsKey(e)) {
			map.put(e, e);
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o, o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return map.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c) {
			if (add(e)) {
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<Entry<E, E>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<E, E> entry = iterator.next();
			if (c.contains(entry.getKey())) {
				iterator.remove();
			}
		}
		return modified;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object e : c) {
			if (remove(e)) {
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		map.clear();
	}

}
