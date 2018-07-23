package com.kmecpp.osmium.api.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {

	public static final char COLOR_CHAR = '\u00A7';

	public static String style(String style, String message) {
		StringBuilder sb = new StringBuilder();
		for (char c : style.toCharArray()) {
			Chat chat = Chat.fromCode(c);
			sb.append(chat == null ? c : chat.toString());
		}
		return sb.append(message != null ? message : "").toString();
	}

	/**
	 * Strips color codes from the given String
	 * 
	 * @param str
	 *            the String to strip
	 * @return the stripped version of the String
	 */
	public static String stripColorCodes(String str) {
		return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', str));
	}

	public static String reverseColor(String str) {
		return str.replace(COLOR_CHAR, '&');
	}

	/**
	 * Colors the String using the Essentials color code: '&'. Equivalent to<br>
	 * <br>
	 * <code>
	 * ChatColor.translateAlternateColorCodes('&', str);
	 * </code>
	 * 
	 * @param str
	 *            the String to color
	 * @return the colored version of the String
	 */
	public static String getColored(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	/**
	 * Strips the given message of all color. Equivalent to <br>
	 * <br>
	 * <code>ChatColor.stripColor(String)</code>
	 * 
	 * @param str
	 *            input String to strip of color
	 * @return a copy of the input String, without coloring
	 */
	public static String stripColor(String str) {
		return ChatColor.stripColor(str);
	}

	/**
	 * Strips Essentials formatting and magic codes from the given String
	 * 
	 * @param str
	 *            the String to strip
	 * @return the stripped version of the String
	 */
	public static String stripFormattingCodes(String str) {
		return str.replaceAll("(?<!&)&([k-orA-FK-OR])", "");
	}

	/**
	 * Strips all Essentials formatting and color codes from the given String
	 * 
	 * @param str
	 *            the String to strip
	 * @return the stripped version of the String
	 */
	public static String stripChatCodes(String str) {
		return stripColorCodes(stripFormattingCodes(str));
	}

	public static void sendNumberedList(CommandSender sender, String title, CS colors, List<?> list) {
		sendTitle(sender, colors, title);
		for (int i = 0; i < list.size(); i++) {
			sender.sendMessage(colors.getTertiary() + " " + (i + 1) + ") " + colors.getSecondary() + list.get(i));
		}
	}

	public static void sendTitle(CommandSender out, String title) {
		sendTitle(out, CS.XEA, title);
	}

	public static void sendItem(CommandSender out, String key, Object value) {
		out.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + key + ": " + ChatColor.GREEN + ChatColor.BOLD + String.valueOf(value));

	}

	public static void sendTitle(CommandSender out, CS colors, String title) {
		out.sendMessage("");
		out.sendMessage(colors.getPrimary() + ChatColor.BOLD.toString() + title);
		out.sendMessage(colors.getSecondary() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------");
		out.sendMessage("");
	}

}
