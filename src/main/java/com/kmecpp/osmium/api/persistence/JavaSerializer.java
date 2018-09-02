package com.kmecpp.osmium.api.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class JavaSerializer {

	/**
	 * Serializes the given object to a string
	 * 
	 * @param obj
	 *            the object to serialize
	 * @return the serialized form of the object
	 * @throws RuntimeException
	 *             if the given object cannot be serialized
	 */
	public static String serialize(Serializable obj) {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try (ObjectOutputStream stream = new ObjectOutputStream(target)) {
			stream.writeObject(obj);
			return new String(target.toByteArray(), StandardCharsets.ISO_8859_1);
		} catch (NotSerializableException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new RuntimeException("Could not serialize the object", e);
		}
	}

	/**
	 * Deserializes an object from the string assuming its class implements the
	 * serializable interface.
	 * 
	 * @param str
	 *            the string to deserialize
	 * @return the object representation of the String
	 * @throws RuntimeException
	 *             if the given string does not represent a valid class
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deserialize(String str) {
		return (T) deserialize(str, Serializable.class);
	}

	/**
	 * Deserializes an object from the string, assuming its class implements the
	 * serializable interface, and then casts it to the specified class
	 * 
	 * @param <T>
	 *            the type of the class
	 * @param str
	 *            the string to deserialize
	 * @param c
	 *            the class to cast the object to
	 * @throws RuntimeException
	 *             if an error occurs during deserialization
	 * @return the object representation of the String
	 * @throws ClassCastException
	 *             if the object is is not assignable to the type
	 */
	public static <T extends Serializable> T deserialize(String str, Class<T> c) {
		ByteArrayInputStream target = new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1));
		try (ObjectInputStream stream = new ObjectInputStream(target)) {
			Object object = stream.readObject();
			return c.cast(object);
		} catch (InvalidClassException e) {
			throw new RuntimeException("Invalid class!", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found!", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not deserialize the string!", e);
		}
	}

}
