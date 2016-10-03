package com.kmecpp.osmium;

import java.util.Optional;

public class Reflection {

	/**
	 * Checks whether or not a class exists.
	 * 
	 * @param className
	 *            the fully qualified name of the class
	 * @return true if the class exists, false if it does not
	 */
	public static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
		}
		return false;
	}

	/**
	 * Attempts to load a class from the fully qualified given class name into
	 * an optional
	 * 
	 * @param className
	 *            the fully qualified class name
	 * @return the class
	 */
	public static Optional<Class<?>> loadClass(String className) {
		try {
			return Optional.of(Class.forName(className));
		} catch (ClassNotFoundException e) {
			return Optional.empty();
		}

	}

}
