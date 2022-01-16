package com.kmecpp.osmium.api.util.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

public class Counter {

	public static <E> HashMap<E, Integer> count(Collection<E> elements) {
		HashMap<E, Integer> counts = new HashMap<>();

		for (E element : elements) {
			counts.put(element, counts.getOrDefault(element, 0) + 1);
		}

		return counts;
	}

	public static <E, K> HashMap<K, Integer> count(Collection<E> elements, Function<E, K> mapper) {
		HashMap<K, Integer> counts = new HashMap<>();

		for (E element : elements) {
			K key = mapper.apply(element);
			counts.put(key, counts.getOrDefault(key, 0) + 1);
		}

		return counts;
	}

	public static <E> List<Entry<E, Integer>> mostCommon(Collection<E> elements) {
		return mostCommon(elements, elements.size());
	}

	public static <E> List<Entry<E, Integer>> mostCommon(Collection<E> elements, int amount) {
		HashMap<E, Integer> counts = count(elements);

		ArrayList<Entry<E, Integer>> sortedEntries = new ArrayList<>(counts.entrySet());
		sortedEntries.sort(Collections.reverseOrder());

		return amount < elements.size() ? sortedEntries.subList(0, amount) : sortedEntries;
	}

	public static <E, K> List<Entry<K, Integer>> mostCommon(Collection<E> elements, Function<E, K> mapper) {
		return mostCommon(elements, mapper, elements.size());
	}

	public static <E, K> List<Entry<K, Integer>> mostCommon(Collection<E> elements, Function<E, K> mapper, int amount) {
		HashMap<K, Integer> counts = count(elements, mapper);

		ArrayList<Entry<K, Integer>> sortedEntries = new ArrayList<>(counts.entrySet());
		sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); //Decreasing value

		return amount < elements.size() ? sortedEntries.subList(0, amount) : sortedEntries;
	}

}
