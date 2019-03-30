package com.kmecpp.osmium.api.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

/**
 * A utility class for manipulating text
 */
public class StringUtil {

	protected StringUtil() {
	}

	public static Object parseType(String str, Class<?> cls) {
		//@formatter:off
		return Reflection.isAssignable(cls, String.class) ? str
				: Reflection.isAssignable(cls, Boolean.class, boolean.class) ? Boolean.parseBoolean(str)
				: Reflection.isAssignable(cls, Integer.class, int.class) ? Integer.parseInt(str)
				: Reflection.isAssignable(cls, Long.class, long.class) ? Long.parseLong(str)
				: Reflection.isAssignable(cls, Float.class, float.class) ? Float.parseFloat(str)
				: Reflection.isAssignable(cls, Double.class, double.class) ? Double.parseDouble(str)
				: Reflection.isAssignable(cls, UUID.class) ? UUID.fromString(str)
				: str;
		//@formatter:on
	}

	public static boolean isMathematicalInteger(String str) {
		if (str.isEmpty()) {
			return false;
		}

		for (int i = (str.charAt(0) == '-' ? 1 : 0); i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	public static String round(double decimal, int places) {
		BigDecimal bd = new BigDecimal(decimal);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return String.valueOf(bd.doubleValue());
	}

	public static String plural(int n) {
		return n == 1 ? "" : "s";
	}

	public static String nth(int n) {
		return n + (n == 1 ? "st" : n == 2 ? "nd" : n == 3 ? "rd" : "th");
	}

	/**
	 * Constructs a new {@link URL} from the String. This method is intended to
	 * be used with hardcoded String URL's and as result rethrows
	 * {@link MalformedURLException}s as a {@link RuntimeException}
	 * 
	 * @param str
	 *            the string to convert to a URL
	 * @return A URL object from the String representation
	 */
	public static URL toURL(String str) {
		try {
			return new URL(str);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the section of the string at the specified index after splitting it
	 * into an array with the given regex
	 * 
	 * @param str
	 *            the string to split
	 * @param regex
	 *            the regex to split the string with
	 * @param index
	 *            the index of the resulting array to retrieve
	 * @return the substring at the given index
	 */
	public static String getSection(String str, String regex, int index) {
		String[] parts = str.split(regex);
		if (parts.length > index) {
			return parts[index];
		}
		return null;
	}

	/**
	 * Counts the number of occurrences of the target substring in the given
	 * string
	 * 
	 * @param str
	 *            the string to search through
	 * @param target
	 *            the target string to search for
	 * @return the number of occurrences of target in str
	 */
	public static int count(String str, String target) {
		return (str.length() - str.replace(target, "").length()) / target.length();
	}

	/**
	 * Ensures the length of all the strings in the array are modified to have
	 * the given length.
	 * This method forwards to
	 * 
	 * <pre>
	 * ensureLength(strings, length, <strong>1</strong>, <strong>true</strong>)
	 * </pre>
	 * 
	 * @param strings
	 *            the strings to modify the length of
	 * @param length
	 *            the new length for all the strings in the array
	 * @return the modified array, for chaining
	 */
	public static String[] ensureLength(String[] strings, int length) {
		return ensureLength(strings, length, 1, true);
	}

	/**
	 * Invokes the ensureLength(str, length, align, favorLeft) method on all of
	 * he strings in the array.
	 * 
	 * @param strings
	 *            the strings to modify the length of
	 * @param length
	 *            the new length for all the strings in the array
	 * @param align
	 *            the align parameter
	 * @param favorLeft
	 *            the favorLeft parameter
	 * @return the modified array, for chaining
	 */
	public static String[] ensureLength(String[] strings, int length, int align, boolean favorLeft) {
		for (int i = 0; i < strings.length; i++) {
			strings[i] = ensureLength(strings[i], length, align, favorLeft);
		}
		return strings;
	}

	/**
	 * <p>
	 * Ensures that the string is modified according to the parameters to have a
	 * length exactly equal to the given one.
	 * </p>
	 * <p>
	 * Forwards to ensureLength(str, length, <strong>1</strong>)
	 * </p>
	 * 
	 * @param str
	 *            the string to modify the length of
	 * @param length
	 *            the new length for the string
	 * @return the modified string according to the rules defined above
	 */
	public static String ensureLength(String str, int length) {
		return ensureLength(str, length, 1);
	}

	/**
	 * <p>
	 * Ensures that the string is modified according to the parameters to have a
	 * length exactly equal to the given one.
	 * </p>
	 * <p>
	 * Forwards to ensureLength(str, length, align, <strong>true</strong>)
	 * </p>
	 * 
	 * @param str
	 *            the string to modify the length of
	 * @param length
	 *            the new length for the string
	 * @param align
	 *            a positive value to modify the string from the left, a
	 *            negative to modify it from the right, and zero to attempt to
	 *            center the modifications
	 * @return the modified string according to the rules defined above
	 */
	public static String ensureLength(String str, int length, int align) {
		return ensureLength(str, length, align, true);
	}

	/**
	 * <p>
	 * Ensures that the string is modified according to the parameters to have a
	 * length exactly equal to the given one.
	 * </p>
	 * <p>
	 * 1) If the string length is the same as the given one, the string is
	 * returned.
	 * </p>
	 * <p>
	 * 2) If the string length is less than the given, it is expanded according
	 * to the <code>align</code> and <code>favorLeft</code> parameters with
	 * whitespace characters. If <code>align</code> is a negative value, the
	 * proper amount of whitespace will be prepended to the string. If it is
	 * positive, the whitespace will be appended to the string. If
	 * <code>align</code> is zero, the string will be surrounded in the same
	 * amount of whitespace, and if it cannot be centered exactly in the middle,
	 * a whitespace character is removed from the right side if
	 * <code>favorLeft</code> is true, and from the left side if it is false.
	 * </p>
	 * <p>
	 * 3) If the string length is less than the given, it is shortened according
	 * to the <code>align</code> and <code>favorLeft</code> parameters by
	 * deleting certain characters. If <code>align</code> is a negative value,
	 * the proper amount of characters will be deleted from the start of the
	 * string. If it is positive, they will be deleted from the end. If
	 * <code>align</code> is zero, the same amount of characters will be deleted
	 * from either ends of the string. If characters cannot be deleted evenly
	 * from each side and still attain the required length, an extra character
	 * is deleted from the right side if <code>favorLeft</code> is true, and
	 * deleted from the left if it is false.
	 * </p>
	 * 
	 * @param str
	 *            the string to modify the length of
	 * @param length
	 *            the new length for the string
	 * @param align
	 *            a positive value to modify the string from the left, a
	 *            negative to modify it from the right, and zero to attempt to
	 *            center the modifications
	 * @param favorLeft
	 *            a control for the case where align is zero, to change whether
	 *            the side of the string that should remain the same, if the
	 *            string cannot be modified evenly on either side
	 * @return the modified string according to the rules defined above
	 */
	public static String ensureLength(String str, int length, int align, boolean favorLeft) {
		int d = length - str.length(); //The difference in lengths from the length parameter and the string length
		if (d == 0) {
			//Same length
			return str;
		} else if (d < 0) {
			//Shorten string
			return align == 0 ? delete(expand(str, d / 2), (d % 2 * (favorLeft ? 1 : -1)))
					: align < 0 ? delete(str, -d)
							: delete(str, length - str.length());
		} else {
			//Expand string
			return align == 0 ? (favorLeft ? "" : whitespace(d % 2)) + expand(str, d / 2) + (favorLeft ? whitespace(d % 2) : "")
					: align < 0 ? (whitespace(d) + str)
							: str + (whitespace(d));
		}
	}

	/**
	 * If the amount is positive, this method expands the string by adding the
	 * given <code>amount</code> of whitespace characters to both ends of the
	 * string. If it is negative, the same amount of characters are removed from
	 * both ends of the string. As a result, the string length is increased or
	 * decreased by double the amount.
	 * 
	 * @param str
	 *            the string to expand
	 * @param amount
	 *            the amount by which to expand the string from either end
	 * @return the expanded string
	 */
	public static String expand(String str, int amount) {
		return amount == 0 ? str
				: amount > 0 ? new StringBuilder(whitespace(amount * 2)).insert(amount, str).toString()
						: (str.length() < 2 * -amount) ? "" : str.substring(-amount, str.length() - -amount);
	}

	/**
	 * Gets the length of the longest string in the given list, and -1 if no
	 * strings are given.
	 * 
	 * @param strings
	 *            the strings to search through
	 * @return the length of the longest string
	 */
	public static int longestLength(String... strings) {
		int max = -1;
		for (String str : strings) {
			if (str.length() > max) {
				max = str.length();
			}
		}
		return max;
	}

	/**
	 * Gets the last character of the string or null if the string is empty
	 * 
	 * @param str
	 *            the string to get the last character from
	 * @return the last character in the string
	 */
	public static Character last(String str) {
		return str.length() > 0
				? str.charAt(str.length() - 1)
				: null;
	}

	/**
	 * Trims the ends of the string which do not match the given regular
	 * expression
	 * 
	 * @param str
	 *            the string to trim
	 * @param regex
	 *            the regex to trim with
	 * @return the trimmed string
	 */
	public static String trim(String str, String regex) {
		int i, j;

		for (i = 0; i < str.length() && !String.valueOf(str.charAt(i)).matches(regex); i++);
		for (j = str.length(); j > 0 && !String.valueOf(str.charAt(j - 1)).matches(regex); j--);

		return str.substring(i, j);
	}

	/**
	 * Trims any leading or trailing zeros from the given string. If the string
	 * is not a number the method will not throw an exception but trim it as if
	 * it were one
	 * 
	 * @param num
	 *            the number to trim
	 * @return the trimmed string
	 */
	public static String trimNumber(String num) {
		String t = trim(num, "[1-9.]");
		return t.charAt(t.length() - 1) == '.' ? t.substring(0, t.length() - 1) : t;
	}

	/**
	 * Forwards to <strong> trimNumber(String)</strong>. This method executes in
	 * the exact same way but takes a double as a parameter.
	 * 
	 * @param num
	 *            the number to trim
	 * @return the trimmed string
	 */
	public static String trimNumber(double num) {
		return trimNumber(String.valueOf(num));
	}

	/**
	 * Deletes the specified amount of characters from the
	 * {@link StringBuilder}. If the amount value is negative the characters are
	 * deleted from right to left
	 * 
	 * @param sb
	 *            the string builder from which to delete
	 * @param amount
	 *            the amount of characters
	 * @return the string builder
	 */
	public static StringBuilder delete(StringBuilder sb, int amount) {
		if (Math.abs(amount) >= sb.length()) {
			sb.setLength(0);
		} else if (amount >= 0) {
			sb.delete(0, amount);
		} else {
			sb.setLength(sb.length() - amount);
		}
		return sb;
	}

	/**
	 * Deletes the specified amount of characters from the String. If the amount
	 * value is positive, the characters are deleted from left to right. If it
	 * is negative, the characters are deleted from right to left. If the amount
	 * is greater than or equal to the length of the string, all characters are
	 * removed and no exception is thrown
	 * 
	 * @param str
	 *            the string from which to delete
	 * @param amount
	 *            the amount of characters
	 * @return a new string with the characters deleted
	 */
	public static String delete(String str, int amount) {
		if (Math.abs(amount) >= str.length()) {
			return "";
		}

		return amount >= 0
				? str.substring(amount)
				: str.substring(0, str.length() + amount);
	}

	/**
	 * Deletes the last character from the {@link StringBuilder} and returns the
	 * builder
	 * 
	 * @param sb
	 *            the string builder to delete from
	 * @return the string builder
	 */
	public static StringBuilder deleteLast(StringBuilder sb) {
		return delete(sb, -1);
	}

	/**
	 * Deletes the last character from the string and returns the result
	 * 
	 * @param str
	 *            the string to delete from
	 * @return the string with the last character removed
	 */
	public static String deleteLast(String str) {
		return delete(str, -1);
	}

	/**
	 * Tests whether or not the string is equal to any of the given strings.
	 * This method is case sensitive.
	 * 
	 * @param str
	 *            the string to test
	 * @param strings
	 *            the strings to test equality with
	 * @return true if the string is equal to any of the given strings case
	 *         sensitive, false otherwise
	 */
	public static boolean equals(String str, String... strings) {
		for (String string : strings) {
			if (str.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether or not the string is equal to any of the given strings.
	 * This method ignores case.
	 * 
	 * @param str
	 *            the string to test
	 * @param strings
	 *            the strings to test equality with
	 * @return true if the string is equal to any of the given strings ignoring
	 *         case, false otherwise
	 */
	public static boolean equalsIgnoreCase(String str, String... strings) {
		for (String string : strings) {
			if (str.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets whether or not the string starts with any of the given prefixes.
	 * This method is case sensitive.
	 * 
	 * @param str
	 *            the string to test
	 * @param prefixes
	 *            the prefixes to test for
	 * @return true if the string starts with any of the prefixes case
	 *         sensitive, and false if it does not
	 */
	public static boolean startsWith(String str, String... prefixes) {
		for (String prefix : prefixes) {
			if (str.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets whether or not the string starts with any of the given prefixes.
	 * This method ignores the case of the prefixes.
	 * 
	 * @param str
	 *            the string to test
	 * @param prefixes
	 *            the prefixes to test for
	 * @return true if the string starts any of the the prefixes ignoring case,
	 *         and false if it does not
	 */
	public static boolean startsWithIgnoreCase(String str, String... prefixes) {
		String lower = str.toLowerCase();
		for (String prefix : prefixes) {
			if (lower.startsWith(prefix.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets whether or not the string contains any of the given sequences. This
	 * method is case sensitive.
	 * 
	 * @param str
	 *            the string to test
	 * @param sequences
	 *            the sequences to search for
	 * @return true if the string contains any of the sequences case sensitive,
	 *         false otherwise
	 */
	public static boolean contains(String str, String... sequences) {
		for (String sequence : sequences) {
			if (str.contains(sequence)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets whether or not the string contains any of the given sequences. This
	 * method ignores the case of the sequences.
	 * 
	 * @param str
	 *            the string to test
	 * @param sequences
	 *            the sequence to search for
	 * @return true if the string contains any of the sequences ignoring case,
	 *         false otherwise
	 */
	public static boolean containsIgnoreCase(String str, String... sequences) {
		String lower = str.toLowerCase();
		for (String sequence : sequences) {
			if (lower.contains(sequence.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets whether or not the string contains all of the given sequences. This
	 * method is case sensitive.
	 * 
	 * @param str
	 *            the string to test
	 * @param sequences
	 *            the sequences to search for
	 * @return true if the string contains all of the sequences case sensitive,
	 *         false otherwise
	 */
	public static boolean containsAll(String str, String... sequences) {
		for (String sequence : sequences) {
			if (!str.contains(sequence)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets whether or not the string contains all of the given sequences. This
	 * method ignores the case of the sequences.
	 * 
	 * @param str
	 *            the string to test
	 * @param sequences
	 *            the sequence to search for
	 * @return true if the string contains all of the sequences ignoring case,
	 *         false otherwise
	 */
	public static boolean containsAllIgnoreCase(String str, String... sequences) {
		String lower = str.toLowerCase();
		for (String sequence : sequences) {
			if (!lower.contains(sequence.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Splits the string into an array of its lines, which it assumes are
	 * separated by '\n' characters.
	 * 
	 * @param str
	 *            the string to split
	 * @return an array of the string's lines
	 */
	public static String[] getLines(String str) {
		return str.split("\n");
	}

	/**
	 * Splits the string into an array of its lines. This method uses the
	 * current systems line separator to split the string. Usually it is better
	 * just use getLines(String) which splits on each '\n' character.
	 * 
	 * @param str
	 *            the string to split
	 * @return an array of the string's lines
	 */
	public static String[] getSystemLines(String str) {
		return str.split(System.lineSeparator());
	}

	/**
	 * Capitalizes the each word in the string by converting the first letter to
	 * upper case and all subsequent letters to lower case
	 * 
	 * @param str
	 *            the String to capitalize
	 * @return the capitalized version of the String
	 */
	public static String capitalize(String str) {
		if (str.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String word : str.split(" ")) {
			sb.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
		}
		return sb.toString();
	}

	/**
	 * Returns either "a" or "an" depending on whether or not the starting
	 * letter in the given string is a vowel. The value will be "an" if the
	 * first letter is a vowel, and "a" if it is not. This method is useful when
	 * displaying a message with a variable word after this specific article.
	 * For example,
	 * 
	 * <br>
	 * 
	 * <pre>
	 * You selected a/an {apple/bar}
	 * </pre>
	 * 
	 * @param str
	 *            the string to test
	 * @return the proper article
	 */
	public static String article(String str) {
		return vowel(str) ? "an" : "a";
	}

	/**
	 * Tests whether or not the given string starts with a vowel, and returns
	 * true if it does and false if it does not
	 * 
	 * @param str
	 *            the String to test
	 * @return true if the String begins with a vowel and false if it does not
	 */
	public static boolean vowel(String str) {
		return str.length() > 0
				? "aeiou".indexOf(Character.toLowerCase(str.charAt(0))) >= 0
				: false;
	}

	/**
	 * Tests whether or not the given string is alphanumeric. A string is
	 * alphanumeric if it contains only ASCII alphabet letters or numbers
	 * 
	 * @param str
	 *            the string to test
	 * @return true if the string is alphanumeric, false if it is not
	 */
	public static boolean alphanumeric(String str) {
		return str.matches("^[a-zA-Z0-9]*$");
	}

	/**
	 * Tests if the string has characters that repeat
	 * 
	 * @param str
	 *            the string to test
	 * @return true if the string has repeating characters, false if it does not
	 */
	public static boolean hasRepeats(String str) {
		if (str.length() > Character.MAX_VALUE) {
			return true;
		}
		BigInteger values = new BigInteger("0");
		for (int i = 0; i < str.length(); i++) {
			int val = str.charAt(i);
			if (values.testBit(val)) {
				return true;
			}
			values = values.setBit(val);
		}
		return false;
	}

	/**
	 * Repeat the given String the specified number of times
	 * 
	 * @param str
	 *            the String to repeat
	 * @param times
	 *            the number of times to repeat
	 * @return the repeated string
	 */
	public static String repeat(String str, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * Repeat the given character the specified number of times to create a
	 * String
	 * 
	 * @param c
	 *            the character to repeat
	 * @param times
	 *            the number of times to repeat
	 * @return the repeated string
	 */
	public static String repeat(char c, int times) {
		char[] chars = new char[times];
		Arrays.fill(chars, c);
		return new String(chars);
	}

	/**
	 * Gets a String of whitespace with the specified amount of characters
	 * 
	 * @param amount
	 *            the amount of whitespace characters
	 * @return the whitespace String
	 */
	public static String whitespace(int amount) {
		return repeat(' ', amount);
	}

	/**
	 * Converts the array to a String delimited by a space <br>
	 * <br>
	 * Effectively the same as calling:
	 * 
	 * <pre>
	 * StringUtil.join(array, " ");
	 * </pre>
	 * 
	 * @param array
	 *            the array whose String representation to return
	 * @return the String representation of the contents of the array
	 */
	public static String join(Object[] array) {
		return join(array, " ");
	}

	/**
	 * Converts the array to a String separated by the given delimiter. This
	 * method forwards to
	 * 
	 * <pre>
	 * join(arr, delimiter, false);
	 * </pre>
	 * 
	 * @param arr
	 *            the array whose String representation to return
	 * @param delimiter
	 *            the separator String
	 * @return the String representation of the contents of the array separated
	 *         by the given delimiter
	 */
	public static String join(Object[] arr, String delimiter) {
		return join(arr, delimiter, false);
	}

	/**
	 * Converts the array to a String separated by the given delimiter. If
	 * <code>ends</code> is true, the delimiter is added to either end of the
	 * string as well.
	 * 
	 * @param arr
	 *            the array whose String representation to return
	 * @param delimiter
	 *            the separator String
	 * @param ends
	 *            whether or not to add the delimiter to the ends of the string
	 * @return the String representation of the contents of the array separated
	 *         by the given delimiter
	 */
	public static String join(Object[] arr, String delimiter, boolean ends) {
		StringBuilder sb = new StringBuilder(ends ? delimiter : "");
		for (int i = 0; i < arr.length; i++) {
			sb.append((i == 0 ? "" : delimiter) + arr[i]);
		}
		return sb.append(ends ? delimiter : "").toString();
	}

	/**
	 * Converts the {@link Iterable} to a {@link String}, separated by the given
	 * delimiter
	 * 
	 * @param iterable
	 *            the iterable whose String representation to return
	 * @param delimiter
	 *            the separator String
	 * @return the String representation of the elements of the iterable
	 *         separated by the given delimiter
	 */
	public static String join(Iterable<?> iterable, String delimiter) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next() + (iterator.hasNext() ? delimiter : ""));
		}
		return sb.toString();
	}

	/**
	 * Gets the stacktrace for the given {@link Throwable} as a string
	 * 
	 * @param throwable
	 *            the throwable whose stacktrace to get
	 * @return a string representation of the throwable
	 */
	public static String getStacktrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	/**
	 * Creates a string from the given {@link Reader} by reading the stream into
	 * a char buffer for more much greater performance than
	 * {@link StringBuilder}
	 * 
	 * @param reader
	 *            the reader to read from
	 * @return a string from the given reader
	 * @throws IOException
	 *             if an I/O occurs while reading from the reader
	 */
	public static String read(Reader reader) throws IOException {
		StringWriter sw = new StringWriter();
		char[] buffer = new char[4096];
		int pos = 0;
		while ((pos = reader.read(buffer)) != -1) {
			sw.write(buffer, 0, pos);
		}
		return sw.toString();
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
	public static String toString(Object object) {
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

}
