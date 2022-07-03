package com.kmecpp.osmium.api.command;

import java.util.ArrayList;

public abstract class CaptureCommandSender {

	public static final String NAME = "Osmium CaptureCommandSender";

	private final ArrayList<String> output = new ArrayList<>();

	public abstract void dispatchCommand(String command);

	public String executeGetString(String command) {
		return String.join("\n", executeGetLines(command));
	}

	public String[] executeGetLines(String command) {
		dispatchCommand(command);
		return extractOutput();
	}

	public void captureMessage(String message) {
		output.add(message);
	}

	public String extractOutputString() {
		return String.join("\n", extractOutput());
	}

	public String[] extractOutput() {
		String[] result = getOutput();
		clearOutput();
		return result;
	}

	public String[] getOutput() {
		return output.toArray(new String[0]);
	}

	public void clearOutput() {
		output.clear();
	}

}
