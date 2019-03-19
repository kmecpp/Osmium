package com.kmecpp.osmium.ap;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.kmecpp.osmium.api.config.ConfigProperties;

@SupportedAnnotationTypes({ "com.kmecpp.osmium.api.config.ConfigProperties" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigTypeProcessor extends AbstractProcessor {

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		//		System.out.println("Remapping custom types");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			if (!roundEnv.errorRaised()) {
				finish();
			}
			return true;
		}

		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ConfigProperties.class);
		if (elements.size() == 0) {
			return false;
		}

		//		for (Element element : elements) {
		//			for (Element e : element.getEnclosedElements()) {
		//				System.out.println(e.getKind());
		//				if (e instanceof Parameterizable) {
		//					System.out.println("YPPPP");
		//					System.out.println(((Parameterizable) e).getTypeParameters().size());
		//				}
		//			}
		//		}
		return true;
	}

	public void finish() {

	}

}
