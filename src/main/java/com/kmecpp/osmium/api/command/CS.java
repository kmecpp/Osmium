package com.kmecpp.osmium.api.command;

import static org.bukkit.ChatColor.WHITE;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;

/**
 * <pre>
 * 0 - BLACK
 * 1 - DARK BLUE
 * 2 - DARK GREEN
 * 3 - DARK AQUA
 * 4 - DARK RED
 * 5 - DARK PURPLE
 * 6 - GOLD
 * 7 - GRAY
 * 8 - DARK GRAY
 * 9 - INDIGO
 * A - GREEN
 * B - AQUA
 * C - RED
 * D - PURPLE
 * E - YELLOW
 * F - WHITE
 * </pre>
 */
public class CS {

	private ChatColor primary = WHITE;
	private ChatColor secondary = WHITE;
	private ChatColor tertiary = WHITE;

	/**
	 * GOLD GREEN
	 */
	public static final CS X6A = create();

	/**
	 * GOLD AQUA
	 */
	public static final CS X6B = create();

	/**
	 * GREEN YELLOW
	 */
	public static final CS XAE = create();

	/**
	 * YELLOW AQUA
	 */
	public static final CS XEA = create();

	/**
	 * YELLOW DARK_AQUA
	 */
	public static final CS XE3 = create();

	/**
	 * GOLD GREEN AQUA
	 */
	public static final CS X6AB = create();

	/**
	 * GREEN RED GOLD
	 */
	public static final CS XAC6 = create();

	/**
	 * AQUA GOLD GREEN
	 */
	public static final CS XB6A = create();

	/**
	 * YELLOW GREEN AQUA
	 */
	public static final CS XEAB = create();

	private static CS create() {
		return new CS(WHITE, WHITE, WHITE);
	}

	static {
		for (Field field : CS.class.getFields()) {
			if (field.getName().startsWith("X") && field.getType() == CS.class) {
				try {
					String[] parts = field.getName().substring(1).split("");
					CS colors = ((CS) field.get(null));
					colors.primary = ChatColor.getByChar(parts[0].toLowerCase());
					colors.secondary = ChatColor.getByChar(parts[1].toLowerCase());
					if (parts.length == 3) {
						colors.tertiary = ChatColor.getByChar(parts[2].toLowerCase());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public CS(ChatColor primary, ChatColor secondary) {
		this(primary, secondary, WHITE);
	}

	public CS(ChatColor primary, ChatColor secondary, ChatColor tertiary) {
		this.primary = primary;
		this.secondary = secondary;
		this.tertiary = tertiary;
	}

	public ChatColor getPrimary() {
		return primary;
	}

	public ChatColor getSecondary() {
		return secondary;
	}

	public ChatColor getTertiary() {
		return tertiary;
	}

}
