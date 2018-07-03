package com.kmecpp.osmium.api.command;

import com.kmecpp.osmium.api.Abstraction;

public interface CommandSender extends Abstraction {

	/**
	 * Checks if this object is an operator
	 *
	 * @return true if this is an operator, otherwise false
	 */
	public boolean isOp();

	/**
	 * Sets whether or not this object is an operator
	 *
	 * @param value
	 *            true to make operator op false to remove operator status
	 */
	public void setOp(boolean value);

	/**
	 * Gets the value of the specified permission, if set.
	 *
	 * @param name
	 *            the permission to check
	 * @return whether or not the object has that permission
	 */
	public boolean hasPermission(String permission);

	String getName();

	void sendMessage(String message);

}
