package com.kmecpp.osmium.api.database.mysql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MySQLTable {

	String name();

	boolean autoCreate() default true;

}
