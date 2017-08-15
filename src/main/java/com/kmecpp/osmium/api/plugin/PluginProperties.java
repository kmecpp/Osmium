package com.kmecpp.osmium.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginProperties {

	String name();

	String version(); //Required by Bukkit

	String description() default "";

	String url() default "";

	String[] authors() default {};

	String[] dependencies() default {};

}
