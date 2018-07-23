package com.kmecpp.osmium.api.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtil {

	/**
	 * Tests whether or not the array contains the given value.
	 * 
	 * @param arr
	 *            the array to search through
	 * @param value
	 *            the value to search for
	 * @return true if the array contains the value, false if it does not
	 */
	public static boolean contains(Object[] arr, Object value) {
		for (Object obj : arr) {
			if (obj == value || obj.equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new array with the given type and length of 0.
	 * 
	 * @param type
	 *            the class of the array type
	 * @param <T>
	 *            the type of the array to create
	 * @return an empty array of the given type
	 */
	public static <T> T[] empty(Class<T> type) {
		return newInstance(type, 0);
	}

	/**
	 * Creates a new array with the given type and dimensions.
	 * 
	 * @param type
	 *            the class of the array type
	 * @param <T>
	 *            the type of the array to create
	 * @param length
	 *            the length of the array to create.
	 * @return a new array with the given type and dimensions
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newInstance(Class<T> type, int... length) {
		return (T[]) Array.newInstance(type, length);
	}

	/**
	 * Gets the first non-array component type of the array. Useful for
	 * retrieving the type of a multidimensional array.
	 * 
	 * @param <T>
	 *            the type of the array
	 * @param array
	 *            the array to get the type of
	 * @return the the type of the array
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<?> getRootType(T[] array) {
		Class<?> type = array.getClass();
		while (type.isArray()) {
			type = type.getComponentType();
		}
		return (Class<T>) type;
	}

	/**
	 * Gets the component type of the array.
	 * 
	 * @param <T>
	 *            the type of the array
	 * 
	 * @param array
	 *            the array to get the component type of
	 * @return the component type of the array
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getComponentType(T[] array) {
		return (Class<T>) array.getClass().getComponentType();
	}

	/**
	 * Gets the length of the longest array out of the ones given.
	 * 
	 * @param <T>
	 *            the type of the array
	 * @param arrays
	 *            the arrays to search through
	 * @return the length of the longest array
	 */
	@SafeVarargs
	public static <T> int longestLength(T[]... arrays) {
		int max = -1;
		for (T[] element : arrays) {
			if (element.length > max) {
				max = element.length;
			}
		}
		return max;
	}

	/**
	 * Flattens the given n-dimensional array to one dimension by combining the
	 * arrays
	 * 
	 * @param <T>
	 *            the type of the array
	 * @param array
	 *            the multidimensional array to flatten
	 * @param cls
	 *            the class of the array type
	 * @return the flattened array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] flatten(Object[] array, Class<T> cls) {
		ArrayList<T> list = new ArrayList<>();
		for (Object obj : array) {
			if (obj.getClass().isArray()) {
				list.addAll(Arrays.asList(flatten((T[]) obj, cls)));
			} else {
				list.add((T) obj);
			}
		}
		return list.toArray(empty(cls));
	}

	/**
	 * Combines the arrays into a single one.
	 * 
	 * @param <T>
	 *            the type of the array
	 * @param arrays
	 *            the arrays to combine
	 * @return a single array containing all the elements of the originals
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] combine(T[]... arrays) {
		if (arrays.length == 0) {
			return (T[]) new Object[0];
		}
		ArrayList<T> list = new ArrayList<>();
		for (int i = 0; i < arrays.length; i++) {
			list.addAll(Arrays.asList(arrays[i]));
		}
		Class<T> type = getComponentType((T[]) arrays);
		return list.toArray(empty(type));
	}

	/**
	 * Transposes the given array so that the rows become columns and the
	 * columns become rows. If the matrix is not rectangular null elements are
	 * substituted in place of unknown values.
	 * 
	 * @param <T>
	 *            the type of the array
	 * @param matrix
	 *            the matrix to transpose
	 * @return the transposed matrix
	 */
	public static <T> T[][] transpose(T[][] matrix) {
		int size = longestLength(matrix);
		T[][] transpose = newInstance(getComponentType(matrix), size);
		for (int col = 0; col < size; col++) {
			transpose[col] = getColumn(matrix, col);
		}
		return transpose;
	}

	/**
	 * Gets the column from the given matrix. If the matrix is not complete and
	 * there are unknown elements, those elements are replaced by null.
	 * 
	 * @param <T>
	 *            the type of the array
	 * @param matrix
	 *            the matrix whose column to get
	 * @param column
	 *            the column index
	 * @return the column of the matrix
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] getColumn(T[][] matrix, int column) {
		T[] columnArray = (T[]) newInstance(getRootType(matrix), matrix.length);
		for (int row = 0; row < matrix.length; row++) {
			columnArray[row] = column < matrix[row].length ? matrix[row][column] : null;
		}
		return columnArray;
	}

}
