package com.kmecpp.osmium;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Dependency {

	String id();

	String version() default "";

	boolean optional() default false;

}
