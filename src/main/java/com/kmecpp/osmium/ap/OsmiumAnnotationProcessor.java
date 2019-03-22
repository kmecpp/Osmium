package com.kmecpp.osmium.ap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.kmecpp.osmium.AppInfo;

import javassist.CannotCompileException;
import javassist.CtClass;

public abstract class OsmiumAnnotationProcessor extends AbstractProcessor {

	public void writeClassToRoot(CtClass ctClass) throws CannotCompileException, IOException {
		FileObject file = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "root");
		ctClass.writeFile(new File(file.toUri()).getParent());
	}

	public BufferedWriter getWriter(String file) throws IOException {
		return new BufferedWriter(processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", file).openWriter());
	}

	public void writeRawFile(String file, String contents) {
		try (BufferedWriter writer = getWriter(file)) {
			writer.write(contents);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create zipped file: '" + file + "'!", e);
		}
	}

	public void info(String message) {
		System.out.println("[" + AppInfo.NAME + "] " + message);
		//		getMessager().printMessage(Kind.NOTE, "[" + Osmium.OSMIUM + "] " + message);
	}

	public void error(String message) {
		System.err.println("[" + AppInfo.NAME + "] " + message);
		//		getMessager().printMessage(Kind.ERROR, "[" + Osmium.OSMIUM + "] " + message);
	}

	public Messager getMessager() {
		return this.processingEnv.getMessager();
	}

}
