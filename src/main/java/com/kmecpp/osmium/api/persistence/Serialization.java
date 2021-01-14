package com.kmecpp.osmium.api.persistence;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;

import com.kmecpp.osmium.api.config.ConfigSerialization;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.Reflection;

@SuppressWarnings("unchecked")
public class Serialization {

	private static final HashMap<Class<?>, SerializationData<?>> types = new HashMap<>();

	static {
		registerDefaultType(byte.class, Byte::parseByte);
		registerDefaultType(short.class, Short::parseShort);
		registerDefaultType(int.class, Integer::parseInt);
		registerDefaultType(long.class, Long::parseLong);
		registerDefaultType(float.class, Float::parseFloat);
		registerDefaultType(double.class, Double::parseDouble);
		registerDefaultType(boolean.class, b -> String.valueOf(b ? 1 : 0), s -> s.equals("1") ? true : false);

		registerDefaultType(Byte.class, Byte::parseByte);
		registerDefaultType(Short.class, Short::parseShort);
		registerDefaultType(Integer.class, Integer::parseInt);
		registerDefaultType(Long.class, Long::parseLong);
		registerDefaultType(Float.class, Float::parseFloat);
		registerDefaultType(Double.class, Double::parseDouble);
		registerDefaultType(Boolean.class, b -> String.valueOf(b ? 1 : 0), s -> s.equals("1") ? true : false);

		registerDefaultType(byte[].class, Arrays::toString, (s) -> get(byte.class, s, Byte::parseByte));
		registerDefaultType(short[].class, Arrays::toString, (s) -> get(short.class, s, Short::parseShort));
		registerDefaultType(int[].class, Arrays::toString, (s) -> get(int.class, s, Integer::parseInt));
		registerDefaultType(long[].class, Arrays::toString, (s) -> get(long.class, s, Long::parseLong));
		registerDefaultType(float[].class, Arrays::toString, (s) -> get(float.class, s, Float::parseFloat));
		registerDefaultType(double[].class, Arrays::toString, (s) -> get(double.class, s, Double::parseDouble));
		registerDefaultType(boolean[].class, Arrays::toString, (s) -> get(boolean.class, s, Boolean::parseBoolean));

		registerDefaultType(char.class, (s) -> s.charAt(0));
		registerDefaultType(char[].class, String::new, String::toCharArray);
		registerDefaultType(String.class, (obj) -> obj == null ? null : "\"" + obj + "\"", String::valueOf);
		registerDefaultType(java.util.Date.class, SerializationUtil.DATE_FORMAT::format, SerializationUtil::parseDate);
		registerDefaultType(java.sql.Date.class, SerializationUtil.DATE_FORMAT::format, SerializationUtil::parseSQLDate);

		//Not default type
		register(UUID.class, UUID::fromString);
		register(File.class, f -> f.getAbsolutePath(), File::new);

		//Path is weird because Paths.get() returns an OS specific subclass
		register((Class<Path>) Paths.get("").getClass(), p -> p.toAbsolutePath().toString(), Paths::get);
		register(Path.class, p -> p.toAbsolutePath().toString(), Paths::get);

		//Class is weird because it throws an exception
		register(Class.class, str -> {
			try {
				return Class.forName(str);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	private static final HashSet<Class<?>> serializableClasses = new HashSet<>();

	public static boolean isSerializable(Class<?> cls) {
		if (serializableClasses.contains(cls)) {
			return true;
		}
		boolean serializable = getData(cls) != null;

		//Cache this result
		if (serializable) {
			serializableClasses.add(cls);
		}

		return serializable;
	}

	public static <T, C> T get(Class<C> componentType, String str, Function<String, C> deserializer) {
		String[] parts = str.split(",");
		Object[] result = (Object[]) Array.newInstance(componentType, parts.length);
		for (int i = 0; i < parts.length; i++) {
			result[i] = Integer.parseInt(parts[i]);
		}
		return (T) result;
	}

	public static <T> SerializationData<T> getData(Class<T> cls) {
		SerializationData<T> data = (SerializationData<T>) types.get(cls);
		if (data == null) {
			try {
				register(cls, cls.getDeclaredMethod("fromString", String.class));
				OsmiumLogger.info("Registering default serialization for " + cls.getName());
			} catch (Exception e) {
				if (cls.isEnum()) {
					try {
						OsmiumLogger.info("Registering default enum serialization for " + cls.getName());
						register(cls, cls.getMethod("valueOf", String.class));
					} catch (Exception ex) {}
				}
			}
			return (SerializationData<T>) types.get(cls);
		}

		return data;
	}

	private static <T> void registerDefaultType(Class<T> cls, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(false, String::valueOf, deserializer));
	}

	private static <T> void registerDefaultType(Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(false, serializer, deserializer));
	}

	private static <T> void register(Class<T> cls, Method deserializationMethod) {
		register(cls, s -> {
			try {
				return (T) deserializationMethod.invoke(s);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	public static <T> void register(Class<T> cls, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(true, String::valueOf, deserializer));
	}

	public static <T> void register(Class<T> cls, Serializer<T> serializer, Deserializer<T> deserializer) {
		types.put(cls, new SerializationData<>(true, serializer, deserializer));
	}

	public static <T> Deserializer<T> getDeserializer(Class<T> type) {
		SerializationData<T> data = (SerializationData<T>) types.get(type);
		return data != null ? data.getDeserializer() : null;
	}

	public static <T> String serialize(T obj) {
		if (obj == null) {
			return "null";
		}

		SerializationData<T> data = (SerializationData<T>) types.get(obj.getClass());
		if (data != null) {
			return data.serialize(obj);
			//			return data.isCustomType() ? "\"" + data.serialize(obj) + "\"" : data.serialize(obj);
		} else {
			return String.valueOf(obj);
		}
		//		throw new IllegalArgumentException("Cannot serialize unregistered config type: " + obj.getClass());
		//		throw new IllegalArgumentException("Cannot serialize unknown class: " + obj.getClass().getName());
	}

	public static <T> String serializeAndQuote(T obj) {
		if (obj == null) {
			return "null";
		}

		SerializationData<T> data = (SerializationData<T>) Serialization.getData(obj.getClass());
		if (data != null) {
			return data.isCustomType() ? "\"" + data.serialize(obj) + "\"" : data.serialize(obj);
		} else {
			return String.valueOf(obj);
		}
	}

	public static <T> T deserialize(Class<T> type, String str) {
		Reflection.initialize(type); //Make sure the class is loaded (they possibly registered it in a static initializer)

		if (str == null || str.equals("null")) {
			return null;
		} else if (str.isEmpty()) {
			return ConfigSerialization.getDefaultFor(type);
		}

		SerializationData<T> data = (SerializationData<T>) types.get(type);
		if (data != null) {
			return data.deserialize(str); //The parser will remove the quotations from custom types
			//			return data.deserialize(data.isCustomType() ? str.substring(1, str.length() - 1) : str);
		} else {
			try {
				Method method = type.getDeclaredMethod("fromString", String.class);
				return (T) method.invoke(null, str);
			} catch (Throwable t) {
				if (type.isEnum()) {
					Deserializer<T> deserializer = (s) -> Reflection.cast(Enum.valueOf(Reflection.cast(type), s.toUpperCase()));
					register(type, deserializer);
					return deserializer.deserialize(str);
				}
				//Ignore
			}
		}
		throw new IllegalArgumentException("Cannot parse unregistered type: " + type.getName());
	}

}
