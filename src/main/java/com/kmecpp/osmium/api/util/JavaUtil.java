package com.kmecpp.osmium.api.util;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

public class JavaUtil {

	public static void printStacktrace() {
		for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
			System.err.println("    " + e);
		}
	}

	public static String defaultToString(Object o) {
		return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	public static <E extends Comparable<? super E>, T extends List<E>> T sorted(T list) {
		Collections.sort(list);
		return list;
	}

	public static <E extends Comparable<? super E>, T extends List<E>> T sorted(T list, Comparator<? super E> c) {
		Collections.sort(list, c);
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] merge(@Nonnull T element, T[] array) {
		T[] result = (T[]) Array.newInstance(element.getClass(), array.length + 1);
		result[0] = element;
		System.arraycopy(array, 0, result, 1, array.length);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] merge(@Nonnull T element1, @Nonnull T element2, T[] array) {
		T[] result = (T[]) Array.newInstance(element1.getClass(), array.length + 2);
		result[0] = element1;
		result[1] = element2;
		System.arraycopy(array, 0, result, 2, array.length);
		return result;
	}

	public static <K, V> Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleEntry<>(key, value);
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Object... data) {
		LinkedHashMap<K, V> map = new LinkedHashMap<>();
		if (data.length % 2 != 0) {
			throw new IllegalArgumentException("Data must have even length");
		}
		for (int i = 0; i < data.length; i += 2) {
			map.put(Reflection.cast(data[i]), Reflection.cast(data[i + 1]));
		}
		return map;
	}

	public static <K, V> LinkedHashMap<K, V> lmap(Object... data) {
		return newLinkedHashMap(data);
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

	public static int hashCode(int... integers) {
		int result = 1;
		for (int i : integers) {
			result = 31 * result + i;
		}
		return result;
	}

}
