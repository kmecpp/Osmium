package com.kmecpp.osmium.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

public class JavaClass extends SimpleJavaFileObject {

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public JavaClass(String className) {
		super(getUri(className), Kind.CLASS);
	}

	private static final URI getUri(String className) {
		try {
			return new URI(className);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Could not parse URI", e);
		}
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return baos;
	}

	public byte[] getByteCode() {
		return baos.toByteArray();
	}

}
