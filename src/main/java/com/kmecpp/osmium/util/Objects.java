package com.kmecpp.osmium.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class Objects {

	/**
	 * Converts the given string to its true type by evaluating valid formats.
	 * If the string is a number it will be converted to an {@link Integer},
	 * {@link Double}, or {@link Long}. If it is a value of "true" or "false"
	 * regardless of case, it will be converted to its respective Boolean. A
	 * value of "null" will return <code>null</code>.
	 * 
	 * <br>
	 * <br>
	 * 
	 * If no valid type can be found the original string is returned.
	 * 
	 * @param str
	 *            the string to convert
	 * @return str converted to its evaluated type
	 */
	public static Object typeEval(String str) {
		Object eval = null;
		//Numbers
		try {
			eval = Integer.parseInt(str);
		} catch (NumberFormatException e1) {
			try {
				eval = Long.parseLong(str);
			} catch (NumberFormatException e2) {
				try {
					eval = Double.parseDouble(str);
				} catch (NumberFormatException e) {
				}
			}
		}
		//Boolean
		if (eval == null) {
			eval = str.equalsIgnoreCase("true") ? (Boolean) true
					: str.equalsIgnoreCase("false") ? false : null;
		}
		//Array
		if (eval == null) {
			try {
				ArrayList<Object> list = new ArrayList<>();
				convert: {
					for (String s : Arrays.asList(str.trim().substring(1, str.length() - 1).split(","))) {
						Object element = typeEval(s.trim());
						list.add(element);
						if (list.get(0).getClass() != element.getClass()) {
							break convert;
						}
					}
					eval = list; //If all elements of the same type
				}
			} catch (Exception e) {
				//Array parse exception
			}
		}
		//Null
		if (eval == null) {
			if (str.equalsIgnoreCase("null")) {
				return null;
			}
		}

		return eval != null ? eval : str;
	}

	/**
	 * Creates a string representation of the given object using its field names
	 * and values.
	 * <br>
	 * <br>
	 * The string is in the following format:
	 * 
	 * <pre>
	 * [field1=value, field2=value, field3=value]
	 * </pre>
	 * 
	 * @param object
	 *            the object to convert to a string
	 * @return a string representation of the object
	 */
	public static String toClassString(Object object) {
		StringBuilder sb = new StringBuilder("[");
		for (Field field : object.getClass().getDeclaredFields()) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			try {
				String value = field.getType().isArray()
						? Arrays.deepToString((Object[]) field.get(object))
						: String.valueOf(field.get(object));
				sb.append(field.getName() + "=" + value + ", ");
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new Error(e);
			}
		}
		sb.setLength(sb.length() - 2);
		return sb.append("]").toString();
	}

	/**
	 * Gets the first object from the given arguments that is not null
	 *
	 * @param objects
	 *            the objects to search through
	 * @return the first non null object
	 */
	public static <T> T firstNonNull(T first, T second) {
		return first != null ? first : second;
	}

	public static boolean equals(Object obj, Object... objects) {
		for (Object o : objects) {
			if (obj.equals(o)) {
				return true;
			}
		}
		return false;
	}

}
