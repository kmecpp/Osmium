package com.kmecpp.osmium.api.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.kmecpp.osmium.api.util.IOUtil;

public class ConfigFormatWriter {

	private final File file;
	private final ConfigData data;
	private ConfigFormat format;

	protected StringBuilder sb = new StringBuilder();
	protected StringBuilder tab = new StringBuilder();

	public ConfigFormatWriter(ConfigData data, File file, ConfigFormat format) {
		this.data = data;
		this.file = file;
		this.format = format;
	}

	public void write() throws IOException {
		//Build text
		writeBlock(data.getRoot());

		//Write to file
		IOUtil.createFile(file);
		IOUtil.write(file, sb.toString());
	}

	private void writeBlock(Block block) {
		if (!block.isRoot()) {
			sb.append('\n');
			ConfigManager.writeKey(sb, block.getName());
			sb.append(format.blockOpen);
			sb.append("\n");
		}

		boolean first = true;
		for (ConfigField field : block.getFields()) {
			if (field.getType().toGenericString().contains("<") && (field.getSetting().type().length == 0 || field.getSetting().type()[0] == Object.class)) {
				new ConfigWriteException("Failed to write Config setting '" + field.getJavaPath() + "'. \nError: generics fields must specify a type parameter.").printStackTrace();
				continue;
			}

			int maps = 0;
			for (Class<?> type : field.getComponentTypes()) {
				if (Map.class.isAssignableFrom(type)) {
					if (++maps > 1) {
						new ConfigWriteException("You cannot nest HashMaps in a config file!").printStackTrace();
						continue;
					}
				}
			}

			//Add comment
			if (!field.getSetting().comment().isEmpty()) {
				if (!first) {
					sb.append('\n');
				}
				for (String line : field.getSetting().comment().split("\n")) {
					sb.append(tab);
					sb.append(format.comment);
					sb.append(" " + line + "\n");
				}
			}

			//Write key value pair
			sb.append(tab);
			ConfigManager.writeKey(sb, field.getName());
			sb.append(": ");
			if (field.getType().isPrimitive()) {
				sb.append(String.valueOf(field.getValue()));
			} else {
				writeValue(field.getType(), field.getValue());
			}
			sb.append('\n');
			first = false;
		}
		for (Block nestedBlock : block.getBlocks()) {
			tab.append(format.tab);
			writeBlock(nestedBlock);
			tab.setLength(tab.length() - format.tab.length);
		}

		if (!block.isRoot()) {
			sb.append(tab);
			if (tab.length() > 0) {
				sb.setLength(sb.length() - 1);
			}
			sb.append(format.blockClose);
			sb.append("\n");
		}
	}

	private void writeValue(Class<?> type, Object value) {
		//Write value
		if (type.isArray()) {
			if (value == null) {
				value = Array.newInstance(type, 0);
			}

			//Write primitive arrays using Arrays.toString()
			if (type.getComponentType().isPrimitive()) {
				if (type == byte[].class) {
					sb.append(Arrays.toString((byte[]) value));
				} else if (type == short[].class) {
					sb.append(Arrays.toString((short[]) value));
				} else if (type == int[].class) {
					sb.append(Arrays.toString((int[]) value));
				} else if (type == long[].class) {
					sb.append(Arrays.toString((long[]) value));
				} else if (type == float[].class) {
					sb.append(Arrays.toString((float[]) value));
				} else if (type == double[].class) {
					sb.append(Arrays.toString((double[]) value));
				} else if (type == boolean[].class) {
					sb.append(Arrays.toString((boolean[]) value));
				} else if (type == char[].class) {
					sb.append(Arrays.toString((char[]) value));
				}
			} else {
				//Write general array
				Object[] arr = (Object[]) value;
				writeList(arr, null, arr == null || arr.length == 0);
			}
		}

		else if (Collection.class.isAssignableFrom(type)) {
			Collection<?> collection = (Collection<?>) value;
			writeList(null, collection, collection == null || collection.size() == 0);
		}

		else if (Map.class.isAssignableFrom(type)) {
			sb.setLength(sb.length() - 2);
			writeMap((Map<?, ?>) value);
		}

		else if (ConfigSerializable.class.isAssignableFrom(type)) {
			ConfigSerializable cs = (ConfigSerializable) value;
			TypeData data = new TypeData();
			cs.write(data);
			writeMap((Map<?, ?>) data.getData());
		}

		else {
			String str = ConfigTypes.serialize(value);
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				sb.append(c);
				if (c == '\n') {
					sb.append(tab);
				}
			}
		}
	}

	private void writeMap(Map<?, ?> map) {
		sb.append(format.blockOpen);
		sb.append("\n");
		tab.append(format.tab);
		for (Entry<?, ?> entry : map.entrySet()) {
			sb.append(tab);
			sb.append(entry.getKey());
			sb.append(format.mapSeparator);
			writeValue(entry.getValue().getClass(), entry.getValue());
			sb.append('\n');
		}
		tab.setLength(tab.length() - format.tab.length);
		sb.append(tab);
		sb.append(format.blockClose);
	}

	private void writeList(Object[] arr, Collection<?> collection, boolean condensed) {
		sb.append(format.listOpen);
		if (!condensed) {
			sb.append('\n');
			tab.append(format.tab);
		}

		//Write elements
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				writeElement(arr[i], i == arr.length - 1);
			}
		} else {
			int i = 0, length = collection.size();
			for (Object obj : collection) {
				writeElement(obj, i == length - 1);
				i++;
			}
		}

		//Write end
		if (!condensed) {
			tab.setLength(Math.max(0, tab.length() - 1));
			sb.append('\n');
			sb.append(tab);
		}
		sb.append(format.listClose);
	}

	private void writeElement(Object value, boolean last) {
		sb.append(tab);
		sb.append(format.listElementPrefix);
		writeValue(value == null ? Object.class : value.getClass(), value);
		if (!last) {
			sb.append(format.listElementSuffix);
			sb.append("\n");
		}
	}

	public static class ConfigFormat {

		private char[] tab = new char[0];
		private char[] comment = new char[0];
		private char[] blockOpen = new char[0];
		private char[] blockClose = new char[0];
		private char[] listOpen = new char[0];
		private char[] listClose = new char[0];
		private char[] mapSeparator = new char[0];
		private char[] listElementPrefix = new char[0];
		private char[] listElementSuffix = new char[0];

		public static Builder builder() {
			return new Builder();
		}

		private ConfigFormat() {
		}

		public static class Builder {

			private ConfigFormat chars = new ConfigFormat();

			public Builder setTab(String tab) {
				chars.tab = tab.toCharArray();
				return this;
			}

			public Builder setComment(String comment) {
				chars.comment = comment.toCharArray();
				return this;
			}

			public Builder setBlockOpen(String blockOpen) {
				chars.blockOpen = blockOpen.toCharArray();
				return this;
			}

			public Builder setBlockClose(String blockClose) {
				chars.blockClose = blockClose.toCharArray();
				return this;
			}

			public Builder setListOpen(String listOpen) {
				chars.listOpen = listOpen.toCharArray();
				return this;
			}

			public Builder setListClose(String listClose) {
				chars.listClose = listClose.toCharArray();
				return this;
			}

			public Builder setMapSeparator(String mapSeparator) {
				chars.mapSeparator = mapSeparator.toCharArray();
				return this;
			}

			public Builder setListElementPrefix(String prefix) {
				chars.listElementPrefix = prefix.toCharArray();
				return this;
			}

			public Builder setListElementSuffix(String suffix) {
				chars.listElementSuffix = suffix.toCharArray();
				return this;
			}

			public ConfigFormat build() {
				return chars;
			}

		}

	}

}
