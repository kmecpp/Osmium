package com.kmecpp.osmium;

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

}
