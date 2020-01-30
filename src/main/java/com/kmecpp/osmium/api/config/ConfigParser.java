package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.util.Reflection;

public class ConfigParser {

	private ConfigData data;
	private File file;

	private int index;
	private int length;
	private char current;
	private char[] chars = new char[1024];

	private StringBuilder path = new StringBuilder();

	private int line = 1;
	private int column = 1;
	private int fieldUpdateCount = 0;

	/**
	 * Creates a new config parser to load the {@link File} contents into the
	 * ConfigData. The {@link ConfigData} should have its structure completely.
	 * If the {@link ConfigData} is null then no class will be updated but the
	 * values will still be accessible with getValues().
	 * 
	 * @param data
	 *            the object to load data into
	 * @param file
	 *            the file to read
	 */
	public ConfigParser(ConfigData data, File file) {
		this.data = data;
		this.file = file;
	}

	public boolean load() throws IOException {
		if (!file.exists()) {
			new ConfigFormatWriter(data, file, Osmium.getConfigManager().getFormat()).write();
		}
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
			int totalRead = 0, bufferRead = 0;
			while ((bufferRead = reader.read(chars, totalRead, chars.length - totalRead)) != -1) {
				char[] newArray = new char[chars.length * 2];
				System.arraycopy(chars, 0, newArray, 0, chars.length);
				chars = newArray;
				totalRead += bufferRead;
			}
			length = totalRead;
		}

		if (chars.length == 0) {
			return data.getFields().size() == 0;
		}

		current = chars[0];
		readBlock(0, null);
		return data.getFields().size() == fieldUpdateCount;
	}

	private void readBlock(int blockNameLength, ConfigField map) {
		skipWhitespaceAndComments();
		while (index < length) {
			if (current == '}') {
				read();
				path.setLength(Math.max(0, path.length() - blockNameLength - 1));
				return;
			}
			readNext(path, map);
			skipWhitespaceAndComments();
		}
	}

	private String readKey() {
		int start = index;
		while (index < length && !Character.isWhitespace(current) && current != ':') {
			read();
			if (index == length) {
				throw getError("Unexpected end of file");
			}
		}
		return substring(start, index);
	}

	@SuppressWarnings({ "unchecked" })
	private void readNext(StringBuilder path, ConfigField mapField) {
		String key = readKey();
		//		System.out.println("KEY: " + key);

		skipWhitespaceAndComments();

		//Read value
		if (current == ':') {
			read();
			skipWhitespaceAndComments();
		} else if (current != '{') {
			throw getError("Unexpected character '" + current + "'");
		}

		//Read block or map
		if (current == '{') {
			read();
			path.append(path.length() == 0 ? key : "." + key);
			ConfigField map = data.getField(path.toString());
			readBlock(key.length(), map);
			return;
		}

		String fullPath = path.length() == 0 ? key : path.toString() + "." + key;
		ConfigField field = data.getField(fullPath);

		if (field == null && mapField != null) {
			field = mapField;
		}

		if (field == null) {
			OsmiumLogger.warn("Config file for '" + data.getProperties().path() + "' contains unknown setting: " + fullPath);

			//Skip entry
			if (current == '[') {
				read();
				while (current != ']') {
					readSingleValue();
					skipWhitespaceAndComments();
					if (current == ',') {
						read();
						skipWhitespaceAndComments();
					}
				}
				read();
			} else {
				readSingleValue();
			}
			return;
		}

		Class<?>[] componentTypes = field.getComponentTypes();
		//			System.out.println(Arrays.toString(componentTypes));
		//			if (componentTypes.length == 0) {
		//				throw new IllegalArgumentException("Type for field cannot be empty: " + field.getJavaPath());
		//			}
		//			if (customType == null) {
		int startType = mapField != null ? 0 : -1;
		Object defaultValue = mapField != null ? ((Map<?, ?>) mapField.getValue()).get(key) : field.getValue();
		Object value = parseValue(field, defaultValue, componentTypes, startType);

		//Write the value to the field
		if (field == mapField) {
			HashMap<String, Object> map = (HashMap<String, Object>) mapField.getValue();
			map.put(key, value);
		} else {
			field.setValue(value);
			fieldUpdateCount++;
		}
	}

	@SuppressWarnings({ "unchecked" })
	private Object parseValue(ConfigField field, Object defaultValue, Class<?>[] componentTypes, int typeIndex) {
		Class<?> currentType = typeIndex == -1 || componentTypes.length == 0 ? field.getType() : componentTypes[typeIndex];
		Object value;

		//Read list
		if (current == '[') {
			boolean array = currentType.isArray();

			//Create default
			Collection<Object> list;
			if (array) {
				list = new ArrayList<>();
			} else if (Collection.class.isAssignableFrom(currentType)) {
				//				if (defaultValue != null) {
				//					list = (Collection<Object>) defaultValue;
				//				} else {
				//Create default collection if user didn't set a default value
				try {
					list = (Collection<Object>) currentType.newInstance();
				} catch (Exception e) {
					throw getError("Failed to initialize collection", e);
				}
				//				}
			} else {
				throw getError("Found list when expecting '" + currentType.getName() + "'");
			}

			read();
			skipWhitespaceAndComments();

			while (current != ']') {
				list.add(parseValue(field, list, componentTypes, typeIndex + 1));

				skipWhitespaceAndComments();
				if (current == ',') {
					read();
					skipWhitespaceAndComments();
				}
			}
			read(); //Read closing bracket

			if (array) {
				Object fieldArray = Array.newInstance(currentType.getComponentType(), list.size());

				int i = 0;
				for (Object element : list) {
					Array.set(fieldArray, i, element);
					i++;
				}
				return fieldArray;
			} else {
				return list;
			}

			//			value = readIntoList(field, array, collection, componentTypes, typeIndex);
		}

		//Read ConfigSerializable
		else if (current == '{') {
			read();
			skipWhitespaceAndComments();
			LinkedHashMap<String, ConfigField> fields = new LinkedHashMap<>(); //Order shouldn't be changed
			Object typeInstance = Reflection.createInstance(currentType);
			for (Field dataField : currentType.getDeclaredFields()) {
				fields.put(ConfigManager.getKey(dataField.getName()), new ConfigField(dataField));
				dataField.setAccessible(true);
			}
			while (current != '}' && index < length) {
				try {
					skipWhitespaceAndComments();
					String key = readKey();
					System.out.println(line + ", " + current);
					System.out.println("KEY: " + key);
					skip(':');
					skipWhitespaceAndComments();

					ConfigField dataField = fields.get(key);
					dataField.getBackingField().set(typeInstance, parseValue(dataField, dataField.getValue(typeInstance), new Class[0], -1));
					//					readNext(path, field, Reflection.createInstance(currentType), typeIndex);
					skipWhitespaceAndComments();
					if (current == ',') {
						read();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			read();
			value = typeInstance;
		}

		//Read single value
		else {
			String stringValue = readSingleValue();
			try {
				value = Serialization.deserialize(currentType, stringValue);
			} catch (Exception e) {
				throw getError("Failed to parse '" + stringValue + "' as " + currentType.getName(), e);
			}
		}
		return value;
	}

	private String readSingleValue() {
		int start = index;

		//Read string
		if (current == '\"') {
			read();
			while (current != '\"' || chars[index - 1] == '\\') {
				read();
			}
			read();
			return substring(start + 1, index - 1);
		}

		//Read null
		else if (current == 'n' && chars[index + 1] == 'u' && chars[index + 2] == 'l' && chars[index + 2] == 'l') {
			read();
			read();
			read();
			read();
			return null;
		}

		//Read regular value (contains no spaces commas or brackets)
		else {
			while (!Character.isWhitespace(current) && current != ',' && current != ']') {
				read();
			}
			return substring(start, index);
		}
	}

	private void skipWhitespaceAndComments() {
		while (Character.isWhitespace(current)) {
			read();
		}
		while (current == '#') {
			skip('\n');
			while (Character.isWhitespace(current)) {
				read();
			}
		}
	}

	/**
	 * Skips to one after the index of the next instance of the character given
	 * 
	 * @param c
	 *            the character to skip
	 */
	private void skip(char c) {
		while (current != c) {
			read();
		}
		read();
	}

	//	private void skipWhitespace() {
	//		while (Character.isWhitespace(current)) {
	//			read();
	//		}
	//	}

	private String substring(int start, int end) {
		char[] str = new char[end - start];
		System.arraycopy(chars, start, str, 0, str.length);
		return new String(str);
	}

	/**
	 * Increments the index by one
	 * 
	 * @return the current index before moving
	 */
	private char read() {
		if (current == '\n') {
			line++;
			column = 1;
		} else {
			column++;
		}
		char read = chars[index++];
		current = chars[index];
		return read;
	}

	private ConfigParseException getError(String message) {
		return getError(message, null);
	}

	private ConfigParseException getError(String message, Throwable t) {
		OsmiumLogger.error("Failed to load config: " + file.getPath());
		message = message + " on line " + line + " column " + column;
		return t == null ? new ConfigParseException(message) : new ConfigParseException(message, t);
	}

}
