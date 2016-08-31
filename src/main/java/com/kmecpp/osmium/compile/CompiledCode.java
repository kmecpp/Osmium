package com.kmecpp.osmium.compile;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class CompiledCode extends SimpleJavaFileObject {

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public CompiledCode(String className) throws Exception {
		super(new URI(className), Kind.CLASS);
	}

	public byte[] getByteCode() {
		return baos.toByteArray();
	}

}
