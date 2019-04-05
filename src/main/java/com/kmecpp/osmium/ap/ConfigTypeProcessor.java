package com.kmecpp.osmium.ap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.kmecpp.osmium.api.config.Setting;

@SupportedAnnotationTypes({ "com.kmecpp.osmium.api.config.Setting" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigTypeProcessor extends OsmiumAnnotationProcessor {

	private static final HashMap<String, String> map = new HashMap<>();
	//	private static final ArrayList<String> lines = new ArrayList<>();

	//	public static void main(String[] args) {
	//		String s = "java.util.HashMap<java.lang.String,java.util.HashMap<java.util.UUID,java.lang.Integer>>";
	//	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			if (!roundEnv.errorRaised()) {
				finish();
			}
			return true;
		}

		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Setting.class);
		if (elements.size() == 0) {
			return false;
		}

		for (Element element : elements) {
			if (element.asType() instanceof DeclaredType) {
				DeclaredType type = (DeclaredType) element.asType();
				if (type.getTypeArguments().isEmpty()) {
					continue;
				}
				String location = element.getEnclosingElement().toString() + "." + element.getSimpleName();
				String typeArgs = type.getTypeArguments().toString();
				map.put(location, typeArgs);
				System.out.println("STARTING TYPE: " + type);
				System.out.println(type.asElement());
				HashMap<String, ArrayList<Object>> result = new HashMap<>();
				generateTypeInfo(type, result);
			}
		}
		return true;
	}

	/*
	 * 
	 * HashMap<String, HashMap<UUID, ArrayList<String>>> map = new HashMap<>();
	 * 
	 */
	public HashMap<String, ArrayList<Object>> generateTypeInfo(DeclaredType type, HashMap<String, ArrayList<Object>> result) {
		ArrayList<Object> current = result.get(type.toString());
		for (TypeMirror typeMirror : type.getTypeArguments()) {
			DeclaredType sub = (DeclaredType) typeMirror;
			if (current == null) {
				current = new ArrayList<>();
				System.out.println("SUB NULL: " + sub);
				result.put(type.asElement().toString(), current);
			}

			if (sub.getTypeArguments().isEmpty()) {
				current.add(sub.toString());
			} else {
				current.add(generateTypeInfo(sub, new HashMap<>()));
			}
		}
		return result;
	}

	public void finish() {
		if (map.isEmpty()) {
			return;
		}

		//		try {
		//			ClassPool pool = ClassPool.getDefault();
		//			CtClass typeClass = pool.makeClass("TYPE_INFO");
		//			CtField field = new CtField(pool.get(HashMap.class.getName()), "map", typeClass);
		//			field.setModifiers(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
		//			CtField.Initializer.byExpr("new HashMap<>();");
		//			typeClass.addField(field);
		//			CtConstructor staticInitializer = typeClass.makeClassInitializer();
		//			for (Entry<String, String> entry : map.entrySet()) {
		//				staticInitializer.insertBefore("map.put(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");");
		//			}
		//
		//			writeClassToRoot(typeClass);
		//		} catch (CannotCompileException | IOException | NotFoundException e) {
		//			e.printStackTrace();
		//		}
	}

}
