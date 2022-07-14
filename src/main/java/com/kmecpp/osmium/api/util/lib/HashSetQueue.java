package com.kmecpp.osmium.api.util.lib;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;

public class HashSetQueue<E> {

	protected HashSet<E> hashSet = new HashSet<>();
	protected ArrayDeque<E> queue = new ArrayDeque<>();

	/**
	 * Run a consumer on at least <i>limit</i> elements that are removed from
	 * the queue
	 * 
	 * @param limit
	 *            the maximum number of elements to process
	 * @param consumer
	 *            the elements processor
	 */
	public void process(int limit, Consumer<E> consumer) {
		for (int i = 0; i < limit && !queue.isEmpty(); i++) {
			consumer.accept(poll());
		}
	}

	public boolean add(E element) {
		if (!hashSet.contains(element)) {
			hashSet.add(element);
			queue.add(element);
			return true;
		}
		return false;
	}

	public E poll() {
		E element = queue.poll();
		hashSet.remove(element);
		return element;
	}

	public void clear() {
		hashSet.clear();
		queue.clear();
	}

	public int size() {
		return queue.size();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public boolean contains(Object o) {
		return hashSet.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return hashSet.containsAll(c);
	}

	public Iterator<E> iterator() {
		return queue.iterator();
	}

	public Object[] toArray() {
		return queue.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	public E element() {
		return queue.element();
	}

	public E peek() {
		return queue.peek();
	}

}
