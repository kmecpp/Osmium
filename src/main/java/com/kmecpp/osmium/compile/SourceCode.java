package com.kmecpp.osmium.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

public class SourceCode extends SimpleJavaFileObject {

	private String contents;

	public SourceCode(String className, String contents) {
		super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.contents = contents;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return contents;
	}

}
