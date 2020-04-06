package com.kmecpp.osmium.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigClass {

	String path();

	String header() default "";

	ConfigFormat format() default ConfigFormat.HOCON;

	boolean allowKeyRemoval() default false;

	boolean loadLate() default false;

	boolean manualLoad() default false;

}
