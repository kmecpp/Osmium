package com.kmecpp.osmium.api.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MoreCollectors {

	public static <K, V> Collector<Entry<K, V>, ?, LinkedHashMap<K, V>> toLinkedMap() {
		return Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new);
	}

}
