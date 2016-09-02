package com.kmecpp.osmium.compile;

import java.util.HashMap;
import java.util.Map;

public class JavaClassLoader extends ClassLoader {

	private final Map<String, JavaClass> classes = new HashMap<>();

	public static JavaClassLoader create() {
		return new JavaClassLoader(ClassLoader.getSystemClassLoader());
	}

	public JavaClassLoader(ClassLoader parent) {
		super(parent);
	}

	public JavaClass getClass(String name) {
		return classes.get(name);
	}

	public JavaClassLoader addClass(JavaClass cc) {
		classes.put(cc.getName(), cc);
		return this;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		JavaClass cc = classes.get(name);
		if (cc == null) {
			return super.findClass(name);
		}
		byte[] byteCode = cc.getByteCode();
		return defineClass(name, byteCode, 0, byteCode.length);
	}

}
