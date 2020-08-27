package com.kmecpp.osmium.api.util;

public class JavaUtil {

	public static String defaultToString(Object o) {
		return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

}
