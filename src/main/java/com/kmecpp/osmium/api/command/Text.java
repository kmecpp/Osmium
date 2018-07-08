package com.kmecpp.osmium.api.command;

public class Text {

	private StringBuilder sb;

	private Text(String message) {
		this.sb = new StringBuilder(message);
	}

	public static Text of(String message) {
		return new Text(message);
	}

	public static Text of(String style, String message) {
		return new Text(ChatUtil.style(style, message));
	}

	public Text append(String text) {
		return append("", text);
	}

	public Text append(String style, String text) {
		sb.append(ChatUtil.style(style, text));
		return this;
	}

	//	public static Text set(Chat... chat) {
	//		StringBuilder sb = new StringBuilder();
	//		for (Chat c : chat) {
	//			sb.append(c);
	//		}
	//		return new Text(sb.toString());
	//	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
