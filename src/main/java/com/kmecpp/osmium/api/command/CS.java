package com.kmecpp.osmium.api.command;

import java.lang.reflect.Field;

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

	/**
	 * WHITE
	 */
	public static final CS XF = create();

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

	private Chat primary = Chat.WHITE;
	private Chat secondary = Chat.WHITE;
	private Chat tertiary = Chat.WHITE;

	public CS(Chat primary, Chat secondary) {
		this(primary, secondary, Chat.WHITE);
	}

	public CS(Chat primary, Chat secondary, Chat tertiary) {
		this.primary = primary;
		this.secondary = secondary;
		this.tertiary = tertiary;
	}

	public Chat getPrimary() {
		return primary;
	}

	public Chat getSecondary() {
		return secondary;
	}

	public Chat getTertiary() {
		return tertiary;
	}

	private static CS create() {
		return new CS(Chat.WHITE, Chat.WHITE, Chat.WHITE);
	}

	public static CS from(String str) {
		if (str.isEmpty()) {
			return CS.XF;
		}
		return new CS(
				Chat.fromCodeElseWhite(str.charAt(0)),
				Chat.fromCodeElseWhite(str.charAt(1)),
				Chat.fromCodeElseWhite(str.charAt(2)));
	}

	static {
		for (Field field : CS.class.getFields()) {
			if (field.getName().startsWith("X") && field.getType() == CS.class) {
				try {
					String[] parts = field.getName().substring(1).split("");
					CS colors = ((CS) field.get(null));
					if (parts.length > 0) {
						colors.primary = Chat.fromCode(parts[0].charAt(0));
					}
					if (parts.length > 1) {
						colors.secondary = Chat.fromCode(parts[1].charAt(0));
					}
					if (parts.length > 2) {
						colors.tertiary = Chat.fromCode(parts[2].charAt(0));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
