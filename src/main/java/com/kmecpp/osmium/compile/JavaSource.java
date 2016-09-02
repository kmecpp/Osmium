package com.kmecpp.osmium.compile;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaSource extends SimpleJavaFileObject {

	private final String code;

	public JavaSource(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}

}
