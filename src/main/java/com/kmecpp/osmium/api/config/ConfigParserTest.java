package com.kmecpp.osmium.api.config;
//package com.kmecpp.osmium.api.config;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//
//import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
//
//import com.kmecpp.osmium.api.logging.OsmiumLogger;
//
//public class ConfigParser {
//
//	private File file;
//
//	private int index;
//	private int length;
//	private char current;
//	private char[] chars = new char[1024];
//
//	//	private int captureSize;
//	//	private char[] capture = new char[1024];
//
//	private int line = 1;
//	private int column = 1;
//
//	private HashMap<String, Object> values = new HashMap<>();
//
//	/**
//	 * Creates a new config parser to load the {@link File} contents into the
//	 * ConfigData. The {@link ConfigData} should have its structure completely.
//	 * If the {@link ConfigData} is null then no class will be updated but the
//	 * values will still be accessible with getValues().
//	 * 
//	 * @param data
//	 *            the object to load data into
//	 * @param file
//	 *            the file to read
//	 */
//	public ConfigParser(File file) {
//		//		this.data = data;
//		this.file = file;
//	}
//
//	public void load() throws IOException {
//		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
//			int totalRead = 0, bufferRead = 0;
//			while ((bufferRead = reader.read(chars, totalRead, chars.length - totalRead)) != -1) {
//				char[] newArray = new char[chars.length * 2];
//				System.arraycopy(chars, 0, newArray, 0, chars.length);
//				chars = newArray;
//				totalRead += bufferRead;
//			}
//			length = totalRead;
//		}
//
//		readBlock(new StringBuilder(), 0);
//	}
//
//	private void readBlock(StringBuilder path, int blockNameLength) {
//		skipToNextSignificantChar();
//		while (index < length) {
//			if (current == '}') {
//				read();
//				path.setLength(Math.max(0, path.length() - blockNameLength - 1));
//				return;
//			}
//			readNext(path);
//			skipToNextSignificantChar();
//		}
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private void readNext(StringBuilder peath) {
//		int start = index;
//		while (!Character.isWhitespace(current) && current != ':') {
//			read();
//			if (index == length) {
//				throw getError("Unexpected end of file");
//			}
//		}
//		String key = substring(start, index);
//		//		System.out.println("KEY: " + key);
//
//		skipToNextSignificantChar();
//
//		if (current == '{') {
//			read();
//			path.append(path.length() == 0 ? key : "." + key);
//			//			ConfigField mapField = data.getField(path.toString());
//			//			HashMap<String, Object> possibleMap = mapField != null ? (HashMap<String, Object>) mapField.getValue() : null;
//			//						if (map != null) {
//			//			
//			//						} else {
//			readBlock(path, key.length());
//			//			}
//		} else if (current == ':') {
//			read();
//			skipToNextSignificantChar();
//			String fullPath = path.length() == 0 ? key : path.toString() + "." + key;
//			Class<?> type;
//			Class<?> componentType;
//
//			//			ConfigField field = data.getField(fullPath);
//			System.out.println(fullPath + " ::  FIELD: " + field);
//			//			if (field == null) {
//			//				OsmiumLogger.warn("Config file for '" + data.getProperties().path() + "' contains unknown setting: " + fullPath);
//			//				return;
//			//			}
//			
//
//			//Read list
//			if (current == '[') {
//				read();
//				skipToNextSignificantChar();
//
//				//				Class<?> componentType;
//				Collection collection;
//				if (type.isArray()) {
//					//					componentType = field.getComponentType();
//					collection = new ArrayList<>();
//				} else if (Collection.class.isAssignableFrom(type)) {
//					//					componentType = field.getSetting().type();
//					collection = (Collection<?>) field.getValue();
//					if (collection == null) {
//						//Create default collection if user didn't set a default value
//						try {
//							collection = (Collection<?>) type.newInstance();
//						} catch (InstantiationException | IllegalAccessException e) {
//							throw getError("Failed to initialize collection", e);
//						}
//					}
//				} else {
//					throw getError("Found list while expecting '" + type.getName() + "'");
//				}
//
//				while (current != ']') {
//					String value = readValue();
//					collection.add(ConfigTypes.deserialize(componentType, value));
//
//					if (current == ',') {
//						read();
//						skipToNextSignificantChar();
//					}
//				}
//				read();//Read closing brace
//
//				if (type.isArray()) {
//					Object fieldArray = Array.newInstance(componentType, collection.size());
//
//					int i = 0;
//					for (Object element : collection) {
//						Array.set(fieldArray, i, element);
//						i++;
//					}
//					field.setValue(fieldArray);
//				}
//			}
//
//			//Read single value
//			else {
//				String value = readValue();
//				field.setValue(ConfigTypes.deserialize(field.getType(), value));
//			}
//		} else {
//			throw getError("Unexpected character '" + current + "'");
//		}
//		return;
//	}
//
//	private void setValue(ConfigField field, HashMap<String, Object> map) {
//
//	}
//
//	private String readValue() {
//		int start = index;
//		if (current == '\"') {
//			read();
//			while (current != '\"' && chars[index - 1] != '\\') {
//				read();
//			}
//			read();
//		} else if (current == 'n') {
//			if (chars[index + 1] == 'u' && chars[index + 2] == 'l' && chars[index + 2] == 'l') {
//				read();
//				read();
//				read();
//				read();
//				return null;
//			}
//		} else {
//			while (!Character.isWhitespace(current) && current != ',' && current != ']') {
//				read();
//			}
//		}
//		//		System.out.println("VALUE: " + substring(start, index));
//		return substring(start, index);
//	}
//
//	private void skipToNextSignificantChar() {
//		skipWhitespace();
//		while (current == '#') {
//			skip('\n');
//			skipWhitespace();
//		}
//	}
//
//	/**
//	 * Skips to one after the index of the next instance of the character given
//	 * 
//	 * @param c
//	 *            the character to skip
//	 */
//	private void skip(char c) {
//		while (current != c) {
//			read();
//		}
//		read();
//	}
//
//	private void skipWhitespace() {
//		while (Character.isWhitespace(current)) {
//			read();
//		}
//	}
//
//	private String substring(int start, int end) {
//		char[] str = new char[end - start];
//		System.arraycopy(chars, start, str, 0, str.length);
//		return new String(str);
//	}
//
//	/**
//	 * Increments the index by one
//	 * 
//	 * @return the current index before moving
//	 */
//	private char read() {
//		if (current == '\n') {
//			line++;
//			column = 1;
//		} else {
//			column++;
//		}
//		char read = chars[index++];
//		current = chars[index];
//		return read;
//	}
//
//	private ConfigParseException getError(String message) {
//		return getError(message, null);
//	}
//
//	private ConfigParseException getError(String message, Throwable t) {
//		message += " on line " + line + " column " + column;
//		return t == null ? new ConfigParseException(message) : new ConfigParseException(message, t);
//	}
//
//}
