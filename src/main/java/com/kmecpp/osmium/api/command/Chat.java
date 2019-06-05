package com.kmecpp.osmium.api.command;

import java.util.ArrayList;
import java.util.HashMap;

public enum Chat {

	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	DARK_PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	GREEN('a'),
	AQUA('b'),
	RED('c'),
	LIGHT_PURPLE('d'),
	YELLOW('e'),
	WHITE('f'),
	MAGIC('k', true),
	BOLD('l', true),
	STRIKETHROUGH('m', true),
	UNDERLINE('n', true),
	ITALIC('o', true),

	RESET('r');

	private static final char COLOR_CHAR = '\u00A7';
	//	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
	private static final HashMap<Character, Chat> chars = new HashMap<>();

	private final char code;
	private final boolean formatting;

	private final String string;

	static {
		for (Chat chat : values()) {
			chars.put(chat.code, chat);
		}
	}

	private Chat(char code) {
		this(code, false);
	}

	private Chat(char code, boolean formatting) {
		this.code = code;
		this.formatting = formatting;
		this.string = new String(new char[] { COLOR_CHAR, code });
	}

	public static char getColorChar() {
		return COLOR_CHAR;
	}

	public char getCode() {
		return code;
	}

	public boolean isFormatting() {
		return formatting;
	}

	public static Chat fromCode(char c) {
		return chars.get(Character.toLowerCase(c));
	}

	public static Chat fromCodeElseWhite(char c) {
		Chat result = chars.get(Character.toLowerCase(c));
		return result != null ? result : WHITE;
	}

	public static String strip(String str) {
		StringBuilder sb = new StringBuilder();
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (i < chars.length - 1 && (chars[i] == '&' || chars[i] == COLOR_CHAR) && Chat.fromCode(chars[i + 1]) != null) {
				i++;
			} else {
				sb.append(chars[i]);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		//		long start = System.nanoTime();
		//		for (int i = 0; i < 10000; i++) {
		//			style("eewefwefweiiwechweiuciwe$cuwicahiuwecawecawceaeafwfSfwewefwefawefaefaewfawefdicksebshits");
		//		}
		//		long end = System.nanoTime();
		//		System.out.println("Time Taken: " + ((end - start) / 1000000F) + "ms");
		//		System.out.println(Chat.styleColor("&a&lBiojwf"));
	}

	public static String styleColor(String message) {
		return styleImpl(message, true, false);
	}

	public static String styleFormatting(String message) {
		return styleImpl(message, false, true);
	}

	public static String style(String message) {
		return styleImpl(message, true, true);
	}

	private static String styleImpl(String message, boolean color, boolean formatting) {
		//		if (!color && !formatting) {
		//			return message;
		//		}
		boolean styled = false;
		char[] chars = message.toCharArray();
		for (int i = 0; i < chars.length - 1; i++) {
			if (chars[i] == '&') {
				Chat code = Chat.fromCode(chars[i + 1]);
				if (code != null && (color && !code.isFormatting() || (formatting && code.isFormatting()))) {
					chars[i] = '\u00A7';
					styled = true;
				}
			}
		}
		return styled ? new String(chars) : message;
	}

	//	public static void main(String[] args) {
	//		System.out.println(styleLines("&aH\n!!&b&lwefij\nhetsw"));
	//		/*
	//		 * &aH
	//		 * &a!!&
	//		 */
	//	}

	public static ArrayList<String> styleLines(String message) {
		ArrayList<String> result = new ArrayList<>();
		char[] currentStyle = new char[2];
		char[] chars = message.toCharArray();
		char[] lineStart = new char[2];
		for (int i = 0, start = 0; i < chars.length - 1; i++) {
			char c = chars[i];
			if (c == '&' && Chat.fromCode(chars[i + 1]) != null) {
				chars[i] = COLOR_CHAR;
				currentStyle[0] = currentStyle[1];
				currentStyle[1] = chars[i + 1];
			}
			if (c == '\n' || i == chars.length - 2) {
				//Carry line style over to next line
				int preCount = lineStart[1] != 0 ? lineStart[0] != 0 ? 4 : 2 : 0;
				int postCount = i == chars.length - 2 ? 2 : 0;
				char[] charResult = new char[preCount + (i - start) + postCount];
				if (lineStart[0] != 0) {
					charResult[0] = COLOR_CHAR;
					charResult[1] = lineStart[0];
				}
				if (lineStart[1] != 0) {
					int offset = preCount == 2 ? -2 : 0;
					charResult[2 + offset] = COLOR_CHAR;
					charResult[3 + offset] = lineStart[1];
				}
				lineStart[0] = currentStyle[0];
				lineStart[1] = currentStyle[1];

				System.arraycopy(chars, start, charResult, preCount, charResult.length - preCount);
				result.add(new String(charResult));
				start = i + 1;

			}
		}
		return result;
	}

	@Override
	public String toString() {
		return string;
	}

}
