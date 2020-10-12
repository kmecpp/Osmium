package com.kmecpp.osmium.api.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.kmecpp.osmium.api.Transient;

public class Reflection {

	public static Class<?> getInvokingClass() {
		return getInvokingClass(0);
	}

	public static Class<?> getInvokingClass(int offset) {
		String className = Thread.currentThread().getStackTrace()[2 + offset].getClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static void walk(Class<?> cls, boolean visitStatic, Consumer<Field> processor) {
		if (cls.getSuperclass() != Object.class) {
			walk(cls.getSuperclass(), visitStatic, processor);
		}

		for (Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(Transient.class) || Modifier.isFinal(field.getModifiers()) || (visitStatic ^ Modifier.isStatic(field.getModifiers()))) {
				continue;
			}

			field.setAccessible(true);
			processor.accept(field);
		}

		for (Class<?> nestedClass : cls.getDeclaredClasses()) {
			if (nestedClass.isAnnotationPresent(Transient.class)) {
				continue;
			}
			walk(nestedClass, visitStatic, processor);
		}
	}

	public static void initialize(Class<?>... classes) {
		for (Class<?> cls : classes) {
			if (cls.isPrimitive()) {
				continue;
			}
			try {
				Class.forName(cls.getName(), true, cls.getClassLoader());
			} catch (ClassNotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	public static String getPackageName(Class<?> cls) {
		String name = cls.getName();
		return name.substring(0, name.lastIndexOf('.'));
	}

	@SuppressWarnings({ "unchecked" })
	public static <T> T createInstance(Class<T> cls) {
		if (cls.isPrimitive()) {
			return (T) getDefaultValue(cls);
		}
		try {
			return cls.newInstance();
		} catch (Exception e) {}
		Constructor<T> c = (Constructor<T>) cls.getDeclaredConstructors()[0];
		c.setAccessible(true);
		Class<?>[] paramClasses = c.getParameterTypes();
		Object[] params = new Object[paramClasses.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = Reflection.getDefaultValue(paramClasses[i]);
		}
		try {
			return c.newInstance(params);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	// These gets initialized to their default values
	private static boolean DEFAULT_BOOLEAN;
	private static byte DEFAULT_BYTE;
	private static short DEFAULT_SHORT;
	private static int DEFAULT_INT;
	private static long DEFAULT_LONG;
	private static float DEFAULT_FLOAT;
	private static double DEFAULT_DOUBLE;

	public static Object getDefaultValue(Class<?> cls) {
		if (cls.equals(boolean.class)) {
			return DEFAULT_BOOLEAN;
		} else if (cls.equals(byte.class)) {
			return DEFAULT_BYTE;
		} else if (cls.equals(short.class)) {
			return DEFAULT_SHORT;
		} else if (cls.equals(int.class)) {
			return DEFAULT_INT;
		} else if (cls.equals(long.class)) {
			return DEFAULT_LONG;
		} else if (cls.equals(float.class)) {
			return DEFAULT_FLOAT;
		} else if (cls.equals(double.class)) {
			return DEFAULT_DOUBLE;
		} else {
			return null;
		}
	}

	public static boolean isImplementation(Class<?> abstractClass, Class<?> implementingClass) {
		return isConcrete(implementingClass) && abstractClass.isAssignableFrom(implementingClass);
	}

	public static boolean isConcrete(Class<?> cls) {
		return !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers());
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	public static void setField(Object obj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void setField(Object obj, String field, Object value) {
		setField(obj.getClass(), obj, field, value);
	}

	public static void setField(Class<?> cls, Object obj, String field, Object value) {
		try {
			getField(cls, field).set(obj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Tests whether or not the class is assignable from ANY of the given
	 * options. Essentially this method calls for each class parameter in the
	 * varargs.
	 * 
	 * <pre>
	 * Class[].isAssignableFrom(cls)
	 * </pre>
	 * 
	 * @param cls
	 *            the class to test
	 * @param classes
	 *            the classes to see if the given one is assignable from
	 * @return true if the class matches any of the ones given, false otherwise.
	 */
	public static boolean isAssignable(Class<?> cls, Class<?>... classes) {
		for (Class<?> c : classes) {
			if (c.isAssignableFrom(cls)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Attempts to invokes the constructor without any parameters and returns
	 * the created instance
	 * 
	 * @param <T>
	 *            the type of the constructor
	 * @param constructor
	 *            the constructor to invoke
	 * @return the instance created
	 */
	public static <T> T newInstance(Constructor<T> constructor) {
		return newInstance(constructor, (Object[]) null);
	}

	public static <T> T newInstance(Constructor<T> constructor, Object... values) {
		try {
			constructor.setAccessible(true);
			return constructor.newInstance((Object[]) values);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T newInstance(Class<T> cls, Object... params) {
		try {
			return getConstructor(cls, params).newInstance(params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets all the constructors of the given class which match the specified
	 * number of parameters.
	 * 
	 * @param <T>
	 *            the type of the class
	 * @param cls
	 *            the class to search to search for constructors
	 * @param params
	 *            the parameter count of the constructors
	 * @return all the constructors of the given class which have the specified
	 *         number of parameters
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<Constructor<T>> getConstructors(Class<T> cls, int params) {
		ArrayList<Constructor<T>> constructors = new ArrayList<>();
		for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
			if (constructor.getParameterTypes().length == params) {
				constructor.setAccessible(true);
				constructors.add((Constructor<T>) constructor);
			}
		}
		return constructors;
	}

	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(Class<T> cls, Object... params) {
		try {
			return (Constructor<T>) findMatch(cls.getDeclaredConstructors(), null, params);
		} catch (Exception e) {
			try {
				Constructor<T> constructor = cls.getDeclaredConstructor(getParamTypes(params));
				constructor.setAccessible(true);
				return constructor;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeStaticMethod(Class<?> cls, String methodName, Object... params) {
		try {
			return (T) getMethod(cls, methodName, params).invoke(null, (Object[]) params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object obj, Method method, Object... params) {
		try {
			method.setAccessible(true);
			return (T) method.invoke(obj, params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T invokeMethod(Object obj, String methodName, Object... params) {
		return invokeMethod(obj.getClass(), obj, methodName, params);
	}

	public static <T> T invokeMethod(Class<?> cls, Object obj, String methodName, Object... params) {
		return invokeMethod(obj, getMethod(cls, methodName, params), params);
	}

	/**
	 * NOTE: This method does NOT handle ambiguous calls how Java does, but does
	 * have fairly decent method signature resolution.
	 * https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12.2
	 * 
	 * @param cls
	 *            the class containing the method
	 * @param methodName
	 *            the method name
	 * @param params
	 *            the method parameters
	 * @return the result of the method
	 */
	public static Method getMethod(Class<?> cls, String methodName, Object... params) {
		if (params == null) {
			params = new Object[] {};
		}
		try {
			return findMatch(cls.getDeclaredMethods(), methodName, params);
		} catch (Exception e) {
			try {
				Method method = cls.getDeclaredMethod(methodName, getParamTypes(params));
				method.setAccessible(true);
				return method;
			} catch (NoSuchMethodException | SecurityException ex) {
				throw new RuntimeException(ex);
			}
		}
		//		try {
		//			ArrayList<Method> potentialMethods = new ArrayList<>();
		//			methodLoop: for (Method method : cls.getDeclaredMethods()) {
		//				if (method.getParameterTypes().length != params.length || !method.getName().equals(methodName)) {
		//					continue;
		//				}
		//				method.setAccessible(true);
		//
		//				boolean exact = true;
		//				for (int i = 0; i < method.getParameterCount(); i++) {
		//					if (!method.getParameterTypes()[i].equals(getClass(params[i]))) {
		//						if (method.getParameterTypes()[i].isAssignableFrom(params[i].getClass())) {
		//							exact = false;
		//						} else {
		//							continue methodLoop;
		//						}
		//					}
		//				}
		//
		//				if (exact) {
		//					return method;
		//				} else {
		//					potentialMethods.add(method);
		//				}
		//			}
		//
		//			if (potentialMethods.size() == 1) {
		//				return potentialMethods.get(0);
		//			} else if (potentialMethods.size() > 1) {
		//				Class<?>[] paramTypes = new Class[params.length];
		//				for (int i = 0; i < params.length; i++) {
		//					paramTypes[i] = getClass(params[i]);
		//				}
		//				Method method = cls.getDeclaredMethod(methodName, paramTypes);
		//				return method;
		//			} else {
		//				throw new NoSuchMethodException();
		//			}
		//		} catch (Exception e) {
		//			throw new RuntimeException("Could not find method " + methodName + " in class: " + cls.getName(), e);
		//		}
	}

	private static <T extends Executable> T findMatch(T[] members, String name, Object... params) throws NoSuchMethodException {
		if (params == null) {
			params = new Object[] {};
		}
		ArrayList<T> potentialMethods = new ArrayList<>();
		methodLoop: for (T member : members) {
			if (member.getParameterTypes().length != params.length || (name != null && !member.getName().equals(name))) {
				continue;
			}
			member.setAccessible(true);

			boolean exact = true;
			for (int i = 0; i < member.getParameterCount(); i++) {
				if (!member.getParameterTypes()[i].equals(getClass(params[i]))) {
					if (member.getParameterTypes()[i].isAssignableFrom(params[i].getClass())) {
						exact = false;
					} else {
						continue methodLoop;
					}
				}
			}

			if (exact) {
				return member;
			} else {
				potentialMethods.add(member);
			}
		}

		if (potentialMethods.size() == 1) {
			return potentialMethods.get(0);
		} else if (potentialMethods.size() > 1) {
			return potentialMethods.get(0);
			//			Class<?>[] paramTypes = new Class[params.length];
			//			for (int i = 0; i < params.length; i++) {
			//				paramTypes[i] = getClass(params[i]);
			//			}
			//			T method = cls.getDeclaredMethod(methodName, paramTypes);
			//			return method;
		} else {
			throw new NoSuchMethodException();
		}
	}

	public static Class<?>[] getParamTypes(Object... params) {
		Class<?>[] types = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			types[i] = getClass(params[i]);
		}
		return types;
	}

	public static Method[] getMethodsWith(Object obj, Class<? extends Annotation> annotation) {
		ArrayList<Method> methods = new ArrayList<>();
		for (Method method : getClass(obj).getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		return methods.toArray(new Method[0]);
	}

	public static <T> T getStaticValue(Field field) {
		return cast(getFieldValue(null, field));
	}

	public static <T> T getFieldValue(Object object, String fieldName) {
		Object instance = object instanceof Class ? null : object;
		return cast(getFieldValue(instance, getField(getClass(object), fieldName)));
	}

	public static <T> T getFieldValue(Object object, Class<?> cls, String fieldName) {
		return getFieldValue(object, getField(cls, fieldName));
	}

	public static <T> T getFieldValue(Object object, Field field) {
		//		return cast(getFieldValue(object, field, Object.class));
		try {
			return cast(field.get(object));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//	public static <T> T getFieldValue(Object object, Field field, Class<T> cast) {
	//		try {
	//			return cast.cast(field.get(object));
	//		} catch (Exception e) {
	//			throw new RuntimeException(e);
	//		}
	//	}

	//	public static Field getFieldOrNull(Object obj, String fieldName) {
	//		try {
	//			return getField(obj, fieldName);
	//		} catch (ReflectionException e) {
	//			return null;
	//		}
	//	}

	public static Field getField(Object obj, String fieldName) {
		try {
			Field field = getClass(obj).getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Field findField(Object obj, String fieldName) {
		Class<?> cls = getClass(obj);
		System.out.println("SEARCHING CLASS: " + cls.getName());

		for (Field field : getClass(obj).getDeclaredFields()) {
			System.out.println("Found: " + field.getType() + " " + field.getName());
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				System.out.println("FOUND!");
				return field;
			}
		}
		return cls.getSuperclass() == null ? null : findField(cls.getSuperclass(), fieldName);
	}

	/**
	 * Gets all the fields from the object with the given annotation. The object
	 * may either be a class or an instance. In either case the fields will
	 * retrieved from that class.
	 * 
	 * @param obj
	 *            the object or class to search
	 * @param annotation
	 *            the annotation to filter for
	 * @return all the fields with the given annotation
	 */
	public static Field[] getFieldsWith(Object obj, Class<? extends Annotation> annotation) {
		ArrayList<Field> fields = new ArrayList<>();
		for (Field field : getClass(obj).getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(annotation)) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Gets all the fields from the object with the given annotation included
	 * inherited fields. The object may either be a class or an instance. In
	 * either case the fields will retrieved from that class.
	 * 
	 * @param obj
	 *            the object or class to search
	 * @param annotation
	 *            the annotation to filter for
	 * @return all the fields with the given annotation
	 */
	public static Field[] getAllFieldsWith(Object obj, Class<? extends Annotation> annotation) {
		ArrayList<Field> fields = new ArrayList<>();

		Class<?> current = getClass(obj);
		while (current != Object.class) {
			Field[] currentFields = current.getDeclaredFields();
			for (int i = currentFields.length - 1; i >= 0; i--) {
				Field field = currentFields[i];
				field.setAccessible(true);
				if (field.isAnnotationPresent(annotation)) {
					fields.add(0, field);
				}
			}
			current = current.getSuperclass();
		}

		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Gets all the fields from the object with types which are assignable to
	 * the given class. The object may either be a class or an instance of one.
	 * 
	 * @param obj
	 *            the object or class to search
	 * @param type
	 *            the type to filter for
	 * @return all the fields matching the given type
	 */
	public static Field[] getFieldsOf(Object obj, Class<?> type) {
		ArrayList<Field> fields = new ArrayList<>();
		for (Field field : getClass(obj).getDeclaredFields()) {
			field.setAccessible(true);
			if (Reflection.isAssignable(field.getType(), type)) {
				fields.add(field);
			}
		}
		return fields.toArray(new Field[fields.size()]);
	}

	public static Field[] getFields(Object obj) {
		Field[] fields = getClass(obj).getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
		}
		return fields;
	}

	public static Class<?> getClass(Object obj) {
		Class<?> cls = obj instanceof Class<?> ? (Class<?>) obj : obj.getClass();
		return cls.isAnonymousClass()
				? cls.getInterfaces().length == 0 ? cls.getSuperclass() : cls.getInterfaces()[0]
				: cls;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 *
	 * @param pkg
	 *            The base package
	 * @return The classes
	 */
	public static Class<?>[] getClasses(String pkg) {
		try {
			Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(pkg.replace('.', '/'));
			System.out.println(pkg.replace('.', '/'));
			System.out.println("RESOURCES: " + resources);
			List<File> files = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				files.add(new File(resources.nextElement().getFile()));
				System.out.println("RESOURCE: " + files.get(files.size() - 1));
			}
			ArrayList<Class<?>> classes = new ArrayList<>();
			for (File directory : files) {
				classes.addAll(findClasses(directory, pkg));
			}
			return classes.toArray(new Class[classes.size()]);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> HashSet<Class<T>> getSubclasses(String pkg, Class<T> cls) {
		HashSet<Class<T>> classes = new HashSet<>();
		for (Class<?> c : getClasses(pkg)) {
			if (isAssignable(c, cls)) {
				classes.add((Class<T>) c);
			}
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 *
	 * @param directory
	 *            The base directory
	 * @param pkg
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 */
	private static List<Class<?>> findClasses(File directory, String pkg) {
		List<Class<?>> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, pkg + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				try {
					classes.add(Class.forName(pkg + '.' + file.getName().substring(0, file.getName().length() - 6)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return classes;
	}

	public static HashSet<Class<?>> getClasses(JarFile jarFile, String pkg) {
		return getClasses(null, jarFile, pkg);
	}

	public static HashSet<Class<?>> getClasses(ClassLoader classLoader, JarFile jarFile, String pkg) {
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		try {
			for (Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements();) {
				String name = entry.nextElement().getName().replace("/", ".");
				if (name.startsWith(pkg) && name.endsWith(".class")) {
					try {
						String className = name.substring(0, name.length() - 6);
						if (classLoader == null) {
							classes.add(Class.forName(className));
						} else {
							classes.add(classLoader.loadClass(className));
						}
					} catch (NoClassDefFoundError e) {
						//Ignore unloaded classes
					}
				}
			}
			jarFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * Checks whether or not a class exists.
	 * 
	 * @param className
	 *            the fully qualified name of the class
	 * @return true if the class exists, false if it does not
	 */
	public static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Attempts to load a class from the fully qualified given class name into
	 * an optional
	 * 
	 * @param className
	 *            the fully qualified class name
	 * @return the class
	 */
	public static Class<?> loadClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private static String[] getSimpleNames(Class<?>[] classes) {
		String[] names = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			names[i] = classes[i].getSimpleName();
		}
		return names;
	}

	public static void printMethods(Class<?> cls) {
		for (Method method : cls.getDeclaredMethods()) {
			System.out.println(method.getReturnType().getName() + " " + method.getName() + "(" + String.join(", ", getSimpleNames(method.getParameterTypes())) + ")");
		}
	}

	public static void printFields(Class<?> cls) {
		for (Field field : cls.getDeclaredFields()) {
			System.out.println(field.getType().getSimpleName() + " " + field.getName());
		}
	}

}
