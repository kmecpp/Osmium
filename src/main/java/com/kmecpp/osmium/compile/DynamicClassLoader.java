package com.kmecpp.osmium.compile;

import java.util.HashMap;
import java.util.Map;

public class DynamicClassLoader extends ClassLoader {

	private Map<String, CompiledCode> customCompiledCode = new HashMap<>();

	public static DynamicClassLoader create() {
		return new DynamicClassLoader(ClassLoader.getSystemClassLoader());
	}

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	public DynamicClassLoader setCode(CompiledCode cc) {
		customCompiledCode.put(cc.getName(), cc);
		return this;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		CompiledCode cc = customCompiledCode.get(name);
		if (cc == null) {
			return super.findClass(name);
		}
		byte[] byteCode = cc.getByteCode();
		return defineClass(name, byteCode, 0, byteCode.length);
	}

}
