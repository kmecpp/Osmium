package com.kmecpp.osmium.api.util;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class JavaUtil {

	public static String defaultToString(Object o) {
		return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	public static <K, V> Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleEntry<>(key, value);
	}

	public static <K, V> LinkedHashMap<K, V> lmap(Object... data) {
		LinkedHashMap<K, V> map = new LinkedHashMap<>();
		if (data.length % 2 != 0) {
			throw new IllegalArgumentException("Data must have even length");
		}
		for (int i = 0; i < data.length; i += 2) {
			map.put(Reflection.cast(data[i]), Reflection.cast(data[i + 1]));
		}
		return map;
	}

	public static <T> T getIndex(Iterable<T> iterable, int index) {
		int i = 0;
		Iterator<T> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			T current = iterator.next();
			if (i == index) {
				return current;
			}
			i++;
		}
		return null;
	}

}
