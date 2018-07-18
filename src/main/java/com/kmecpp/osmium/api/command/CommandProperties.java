package com.kmecpp.osmium.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandProperties {

	String[] aliases();

	String description() default "";

	String permission() default "";

	String usage() default "";

	boolean admin() default false;

	boolean playersOnly() default false;

}
