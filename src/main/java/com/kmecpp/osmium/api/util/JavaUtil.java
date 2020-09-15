package com.kmecpp.osmium.api.util;

import java.util.Iterator;

public class JavaUtil {

	public static String defaultToString(Object o) {
		return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
	}

	public static String getOSName() {
		return System.getProperty("os.name");
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
