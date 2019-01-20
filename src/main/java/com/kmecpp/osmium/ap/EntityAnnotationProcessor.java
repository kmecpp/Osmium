package com.kmecpp.osmium.ap;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes({ "com.kmecpp.osmium.api.plugin.Plugin" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EntityAnnotationProcessor extends AbstractProcessor {

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		//		System.out.println("Remapping custom types");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		//		if (roundEnv.processingOver()) {
		//			if (!roundEnv.errorRaised()) {
		//				finish();
		//			}
		//			return true;
		//		}
		//
		//		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Plugin.class);
		//		if (elements.size() == 0) {
		//			return false;
		//		}
		return true;
	}

}
