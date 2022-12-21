package com.kmecpp.osmium.api.database.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {

	String name();

	DatabaseType[] type() default DatabaseType.SQLITE;

	boolean autoCreate() default true;
	
	boolean autoSave() default true;

}
