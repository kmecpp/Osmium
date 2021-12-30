package com.kmecpp.osmium.api.util.lib;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.function.Consumer;

public class HashSetQueue<T> {

	protected HashSet<T> queueSet = new HashSet<>();
	protected ArrayDeque<T> queue = new ArrayDeque<>();

	/**
	 * Run a consumer on at least <i>limit</i> Players that are removed from the
	 * queue
	 * 
	 * @param limit
	 *            the maximum number of players to process
	 * @param consumer
	 *            the player processor
	 */
	public void process(int limit, Consumer<T> consumer) {
		for (int i = 0; i < limit && !queue.isEmpty(); i++) {
			T element = queue.poll();
			queueSet.remove(element);
			consumer.accept(element);
		}
	}

	public void add(T element) {
		if (!queueSet.contains(element)) {
			queue.add(element);
		}
	}

}
