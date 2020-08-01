package com.kmecpp.osmium.api.database.sqlite;

/**
 * An interface for defining custom serialization for a class to use primarily
 * with the database.
 *
 * If this is used for a custom database type, the implementing class MUST also
 * have a constructor with a single String parameter.
 * 
 * By default the serialize() method just returns
 * toString() however this can be overridden if necessary.
 * 
 * This has significant performance improvements over using Java's
 * generic serialization.
 */
public interface CustomSerialization {

	default String serialize() {
		return toString();
	}

}
