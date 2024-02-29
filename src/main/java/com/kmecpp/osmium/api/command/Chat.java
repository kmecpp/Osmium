package com.kmecpp.osmium.api.command;

import java.util.ArrayList;
import java.util.HashMap;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

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

	public static final char COLOR_CHAR = '\u00A7';
	public static final String COLOR = "\u00A7";
	//	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
	private static final HashMap<Character, Chat> chars = new HashMap<>();

	private final char code;
	private final boolean formatting;
	private String ansi;

	private final String string;

	static {
		for (Chat chat : values()) {
			chars.put(chat.code, chat);
		}

		Chat.DARK_BLUE.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString();
		Chat.DARK_BLUE.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString();
		Chat.DARK_GREEN.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString();
		Chat.DARK_AQUA.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString();
		Chat.DARK_RED.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString();
		Chat.DARK_PURPLE.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString();
		Chat.GOLD.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString();
		Chat.GRAY.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString();
		Chat.DARK_GRAY.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString();
		Chat.BLUE.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString();
		Chat.GREEN.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString();
		Chat.AQUA.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString();
		Chat.RED.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold().toString();
		Chat.LIGHT_PURPLE.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString();
		Chat.YELLOW.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString();
		Chat.WHITE.ansi = Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString();
		Chat.MAGIC.ansi = Ansi.ansi().a(Attribute.BLINK_SLOW).toString();
		Chat.BOLD.ansi = Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString();
		Chat.STRIKETHROUGH.ansi = Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString();
		Chat.UNDERLINE.ansi = Ansi.ansi().a(Attribute.UNDERLINE).toString();
		Chat.ITALIC.ansi = Ansi.ansi().a(Attribute.ITALIC).toString();
		Chat.RESET.ansi = Ansi.ansi().a(Attribute.RESET).toString();
	}

	private Chat(char code) {
		this(code, false);
	}

	private Chat(char code, boolean formatting) {
		this.code = code;
		this.formatting = formatting;
		this.string = new String(new char[] { COLOR_CHAR, code });
	}

	public static String of(boolean bool) {
		return String.valueOf(bool ? Chat.GREEN : Chat.RED);
	}

	public static String status(boolean enabled) {
		return String.valueOf(enabled ? Chat.GREEN + "enabled" : Chat.RED + "disabled");
	}

	public static char getColorChar() {
		return COLOR_CHAR;
	}

	public char getCode() {
		return code;
	}

	public String ansi() {
		return ansi;
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

	/*
	 * 80 spaces
	 * a-e: 52
	 * f: 63
	 */

	@Override
	public String toString() {
		return string;
	}

}
