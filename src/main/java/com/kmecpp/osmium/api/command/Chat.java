package com.kmecpp.osmium.api.command;

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

	//	public static void main(String[] args) {
	//		long start = System.nanoTime();
	//		for (int i = 0; i < 10000; i++) {
	//			style("eewefwefweiiwechweiuciwe$cuwicahiuwecawecawceaeafwfSfwewefwefawefaefaewfawefdicksebshits");
	//		}
	//		long end = System.nanoTime();
	//		System.out.println("Time Taken: " + ((end - start) / 1000000F) + "ms");
	//	}

	public static String style(String message) {
		//		if (!message.contains("&")) {
		//			return message;
		//		}
		if (message == null) {
			return "";
		}

		boolean styled = false;
		char[] chars = message.toCharArray();
		for (int i = 0; i < chars.length - 1; i++) {
			if (chars[i] == '&' && Chat.fromCode(chars[i + 1]) != null) {
				chars[i] = COLOR_CHAR;
				styled = true;
			}
		}
		return styled ? new String(chars) : message;
	}

	@Override
	public String toString() {
		return string;
	}

}
